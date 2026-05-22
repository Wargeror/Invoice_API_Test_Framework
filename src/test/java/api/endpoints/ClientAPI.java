package api.endpoints;

import api.base.BaseAPI;
import api.base.Endpoint;
import api.dto_data_transfer_object.Client;
import io.restassured.response.Response;

import java.util.Map;

public class ClientAPI extends BaseAPI {

    public ClientAPI(String token) {
        super(token);
    }

    public Response createClient(Client client) {
        return post(Endpoint.CLIENTS.getPath(), client);
    }

    public Response getClient(int id) {
        return get(Endpoint.CLIENTS.getPath(), id);
    }

    public Response getClients() {
        return getAll(Endpoint.CLIENTS.getPath());
    }

    public Response deleteClient(int id) {
        return delete(Endpoint.CLIENTS.getPath(), id);
    }

    public Response patchClient(int id, Map<String, Object> fields) {
        return patch(Endpoint.CLIENTS.getPath(), id, fields);
    }
}
