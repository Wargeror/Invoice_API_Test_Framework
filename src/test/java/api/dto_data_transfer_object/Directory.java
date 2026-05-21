package api.dto_data_transfer_object;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Directory {
    // ID of the directory
    private Integer id;
    // Parent ID of the directory (null if in root)
    @SerializedName("parent_id")
    private Integer parentId;
    private String name;

    public Directory(String name) {
        this.name = name;
    }
}
