package api.dto_data_transfer_object;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for File
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class File {
    private Integer id;
    private String filename;
    @SerializedName("client_id")
    private Integer clientId;
    @SerializedName("directory_id")
    private Integer directoryId;
    @SerializedName("file_type")
    private String fileType;
    private String notes;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("send_status")
    private String sendStatus;
    private String confirmation;
    @SerializedName("created_by")
    private Integer createdBy;
}
