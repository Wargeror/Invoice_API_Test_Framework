package api.tests;

import api.base.BaseTest;
import api.dto_data_transfer_object.Invoice;
import api.dto_data_transfer_object.InvoiceItem;
import api.endpoints.InvoiceAPI;
import api.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("API Tests")
@Feature("Invoice Management")
@Tag("api")
@Tag("invoice")
@DisplayName("Invoice API Test Suite")
public class InvoiceAPITest extends BaseTest {
    private InvoiceAPI invoiceAPI;
    private final List<Integer> createdInvoiceIds = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    public void setUpInvoiceAPI() {
        invoiceAPI = new InvoiceAPI(token);
    }

    @AfterEach
    public void tearDownInvoices() {
        for (int invoiceId : createdInvoiceIds) {
            logger.debug("Cleaning up created invoice ID: {}", invoiceId);
            invoiceAPI.deleteInvoice(invoiceId);
        }
        createdInvoiceIds.clear();
    }

    private Invoice createValidInvoiceDTO() {
        Invoice invoice = new Invoice();
        invoice.setToName("Test Client " + Utils.generateRandomString(5));
        invoice.setToAddress("Test Address" + Utils.randomNumeric(5));
        invoice.setToEgn(Utils.generateValidEGN());
        invoice.setToMol("Test Mol " + Utils.generateRandomString(5));
        invoice.setToBulstat(Utils.generateValidBulstat9());
        invoice.setToIsRegVat(true);
        invoice.setToVatNumber(Utils.generateValidVAT());

        InvoiceItem item = new InvoiceItem("Test Item", 10.5f, 2f, "pcs");
        invoice.setItems(Collections.singletonList(item));

        return invoice;
    }

    @Test
    @Story("Create a new invoice")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can create a new invoice")
    @Description("Verify that a new invoice can be successfully created via a POST request to the /invoices endpoint.")
    @Tag("positive")
    public void canCreateNewInvoice() {
        Invoice invoice = createValidInvoiceDTO();
        logger.debug("Attempting to create invoice for client: {}", invoice.getToName());

        Response createResponse = invoiceAPI.createInvoice(invoice);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Failed to create invoice. Expected HTTP 201 Created but received: " + createResponse.statusCode());
        logger.info("Invoice created successfully with status 201.");

        int id = createResponse.jsonPath().getInt("id");
        createdInvoiceIds.add(id);
        Assertions.assertTrue(id > 0, "Invoice creation failed: The returned ID (" + id + ") is invalid or missing.");
        logger.debug("Generated Invoice ID: {}", id);

        Response getResponse = invoiceAPI.getInvoice(id);
        Assertions.assertEquals(200, getResponse.statusCode(),
                "Failed to fetch the newly created invoice. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        Invoice createdInvoice = gson.fromJson(responseBody, Invoice.class);
        Assertions.assertEquals(invoice.getToName(), createdInvoice.getToName(),
                "Data mismatch: The created invoice's recipient name ('" + createdInvoice.getToName() + "') does not match the requested name ('" + invoice.getToName() + "').");
        logger.info("Invoice data verified successfully.");
    }

    @Test
    @Story("Retrieve all invoices")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can get a list of all invoices")
    @Description("Verify that a list of all invoices can be retrieved via a GET request to the /invoices endpoint.")
    @Tag("positive")
    public void canGetAllInvoices() {
        logger.debug("Sending request to fetch all invoices.");
        Response response = invoiceAPI.getInvoices();

        Assertions.assertEquals(200, response.statusCode(),
                "Failed to retrieve invoices list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("invoices"),
                "The response did not contain an 'invoices' list or it was null.");
        logger.info("Invoices retrieved successfully.");
    }

    @Test
    @Story("Partially update an invoice")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Can partially update an invoice")
    @Description("Verify that an invoice's information can be partially updated via a PATCH request to the /invoices/{id} endpoint.")
    @Tag("positive")
    public void canPartiallyUpdateInvoice() {
        Invoice invoice = createValidInvoiceDTO();
        logger.debug("Creating initial invoice for update test: {}", invoice.getToName());

        Response createResponse = invoiceAPI.createInvoice(invoice);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not create initial invoice. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdInvoiceIds.add(id);

        Map<String, Object> fields = new HashMap<>();
        fields.put("payment_currency", "EUR");

        logger.debug("Attempting to PATCH invoice ID {} with new currency: EUR", id);
        Response updateResponse = invoiceAPI.patchInvoice(id, fields);
        Assertions.assertEquals(200, updateResponse.statusCode(),
                "Failed to update invoice. Expected HTTP 200 OK but received: " + updateResponse.statusCode());
        logger.info("Invoice updated successfully.");

        Response getResponse = invoiceAPI.getInvoice(id);
        Assertions.assertEquals(200, getResponse.statusCode(),
                "Failed to fetch the updated invoice. Expected HTTP 200 OK but received: " + getResponse.statusCode());

        String responseBody = getResponse.getBody().asString();
        Invoice fetchedInvoice = gson.fromJson(responseBody, Invoice.class);

        Assertions.assertEquals("EUR", fetchedInvoice.getPaymentCurrency(),
                "Partial update failed: The currency was not updated to 'EUR'. Current value is: '" + fetchedInvoice.getPaymentCurrency() + "'.");
        Assertions.assertEquals(invoice.getToName(), fetchedInvoice.getToName(),
                "Partial update failed: The recipient name was unexpectedly altered during the currency update.");
        logger.info("Invoice partial update verified successfully.");
    }

    @Test
    @Story("Delete an invoice")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Can delete an invoice")
    @Description("Verify that an invoice can be successfully deleted via a DELETE request to the /invoices/{id} endpoint.")
    @Tag("positive")
    public void canDeleteInvoice() {
        Invoice invoice = createValidInvoiceDTO();
        logger.debug("Creating initial invoice for deletion test.");

        Response createResponse = invoiceAPI.createInvoice(invoice);
        Assertions.assertEquals(201, createResponse.statusCode(),
                "Setup failed: Could not create initial invoice. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");
        createdInvoiceIds.add(id);

        logger.debug("Attempting to delete invoice ID: {}", id);
        Response deleteResponse = invoiceAPI.deleteInvoice(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(),
                "Failed to delete invoice. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Invoice deleted successfully.");
        createdInvoiceIds.remove(Integer.valueOf(id));

        Response getResponse = invoiceAPI.getInvoice(id);
        Assertions.assertEquals(404, getResponse.statusCode(),
                "Deletion verification failed: The invoice still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("Invoice absence verified successfully.");
    }
}