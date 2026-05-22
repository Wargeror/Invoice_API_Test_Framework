package api.endpoints;

import api.base.BaseAPI;
import api.base.Endpoint;
import api.dto_data_transfer_object.Invoice;
import io.restassured.response.Response;

import java.util.Map;

public class InvoiceAPI extends BaseAPI {

    public InvoiceAPI(String token) {
        super(token);
    }

    public Response createInvoice(Invoice invoice) {
        return post(Endpoint.INVOICES.getPath(), invoice);
    }

    public Response getInvoice(int id) {
        return get(Endpoint.INVOICES.getPath(), id);
    }

    public Response getInvoices() {
        return getAll(Endpoint.INVOICES.getPath());
    }

    public Response deleteInvoice(int id) {
        return delete(Endpoint.INVOICES.getPath(), id);
    }

    public Response updateInvoice(int id, Invoice invoice) {
        return put(Endpoint.INVOICES.getPath(), id, invoice);
    }

    public Response patchInvoice(int id, Map<String, Object> fields) {
        return patch(Endpoint.INVOICES.getPath(), id, fields);
    }
}
