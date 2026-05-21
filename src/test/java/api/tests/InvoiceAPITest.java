package api.tests;

import api.dto_data_transfer_object.Invoice;
import api.dto_data_transfer_object.InvoiceItem;
import api.endpoints.InvoiceAPI;
import api.utils.TokenManager;
import api.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Tag("api")
@Tag("invoice")
@DisplayName("Invoice API Test")
public class InvoiceAPITest {
    private static final Logger logger = LogManager.getLogger(InvoiceAPITest.class);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
    @Tag("positive")
    @DisplayName("Can create new invoice")
    public void canCreateNewInvoice() {
        logger.info("Starting test: canCreateNewInvoice");
        String token = TokenManager.getToken();
        InvoiceAPI invoiceAPI = new InvoiceAPI(token);

        Invoice invoice = createValidInvoiceDTO();
        logger.debug("Attempting to create invoice for client: {}", invoice.getToName());

        Response createResponse = invoiceAPI.createInvoice(invoice);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Failed to create invoice. Expected HTTP 201 Created but received: " + createResponse.statusCode());
        logger.info("Invoice created successfully with status 201.");

        int id = createResponse.jsonPath().getInt("id");
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

        // Cleanup
        logger.debug("Cleaning up created invoice ID: {}", id);
        Response deleteResponse = invoiceAPI.deleteInvoice(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test invoice. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can get all invoices")
    public void canGetAllInvoices() {
        logger.info("Starting test: canGetAllInvoices");
        String token = TokenManager.getToken();
        InvoiceAPI invoiceAPI = new InvoiceAPI(token);

        logger.debug("Sending request to fetch all invoices.");
        Response response = invoiceAPI.getInvoices();

        Assertions.assertEquals(200, response.statusCode(), 
            "Failed to retrieve invoices list. Expected HTTP 200 OK but received: " + response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getList("invoices"), 
            "The response did not contain an 'invoices' list or it was null.");
        logger.info("Invoices retrieved successfully. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can partially update invoice")
    public void canPartiallyUpdateInvoice() {
        logger.info("Starting test: canPartiallyUpdateInvoice");
        String token = TokenManager.getToken();
        InvoiceAPI invoiceAPI = new InvoiceAPI(token);

        Invoice invoice = createValidInvoiceDTO();
        logger.debug("Creating initial invoice for update test: {}", invoice.getToName());

        Response createResponse = invoiceAPI.createInvoice(invoice);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not create initial invoice. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

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

        // Cleanup
        logger.debug("Cleaning up created invoice ID: {}", id);
        Response deleteResponse = invoiceAPI.deleteInvoice(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Cleanup failed: Unable to delete the test invoice. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Cleanup successful. Test finished.");
    }

    @Test
    @Tag("positive")
    @DisplayName("Can delete invoice")
    public void canDeleteInvoice() {
        logger.info("Starting test: canDeleteInvoice");
        String token = TokenManager.getToken();
        InvoiceAPI invoiceAPI = new InvoiceAPI(token);

        Invoice invoice = createValidInvoiceDTO();
        logger.debug("Creating initial invoice for deletion test.");
        
        Response createResponse = invoiceAPI.createInvoice(invoice);
        Assertions.assertEquals(201, createResponse.statusCode(), 
            "Setup failed: Could not create initial invoice. Expected 201, got: " + createResponse.statusCode());

        int id = createResponse.jsonPath().getInt("id");

        logger.debug("Attempting to delete invoice ID: {}", id);
        Response deleteResponse = invoiceAPI.deleteInvoice(id);
        Assertions.assertEquals(204, deleteResponse.statusCode(), 
            "Failed to delete invoice. Expected HTTP 204 No Content but received: " + deleteResponse.statusCode());
        logger.info("Invoice deleted successfully.");

        Response getResponse = invoiceAPI.getInvoice(id);
        Assertions.assertEquals(404, getResponse.statusCode(), 
            "Deletion verification failed: The invoice still exists. Expected HTTP 404 Not Found but received: " + getResponse.statusCode());
        logger.info("Invoice absence verified successfully. Test finished.");
    }
}
