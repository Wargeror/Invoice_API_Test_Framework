package api.endpoints;

import api.base.BaseAPI;
import api.base.Endpoint;
import api.dto_data_transfer_object.Item;
import io.restassured.response.Response;

import java.util.Map;

public class ItemAPI extends BaseAPI {

    public ItemAPI(String token) {
        super(token);
    }

    public Response createItem(Item item) {
        return post(Endpoint.ITEMS.getPath(), item);
    }

    public Response getItem(int id) {
        return get(Endpoint.ITEMS.getPath(), id);
    }

    public Response getItems() {
        return getAll(Endpoint.ITEMS.getPath());
    }

    public Response deleteItem(int id) {
        return delete(Endpoint.ITEMS.getPath(), id);
    }

    public Response updateItem(int id, Item updateItem) {
        return put(Endpoint.ITEMS.getPath(), id, updateItem);
    }

    public Response patchItem(int id, Map<String, Object> fields) {
        return patch(Endpoint.ITEMS.getPath(), id, fields);
    }
}
