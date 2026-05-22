package api.endpoints;

import api.base.BaseAPI;
import api.base.Endpoint;
import api.dto_data_transfer_object.Directory;
import io.restassured.response.Response;

public class DirectoryAPI extends BaseAPI {

    public DirectoryAPI(String token) {
        super(token);
    }

    public Response createDirectory(Directory directory) {
        return post(Endpoint.DIRECTORIES.getPath(), directory);
    }

    public Response getDirectory(int id) {
        return get(Endpoint.DIRECTORIES.getPath(), id);
    }

    public Response getDirectories() {
        return getAll(Endpoint.DIRECTORIES.getPath());
    }

    public Response deleteDirectory(int id) {
        return delete(Endpoint.DIRECTORIES.getPath(), id);
    }

    public Response updateDirectory(int id, Directory directory) {
        return put(Endpoint.DIRECTORIES.getPath(), id, directory);
    }
}
