package api.dto_data_transfer_object;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Item {

    private String name;
    private String currency;

    @SerializedName("quantity_unit")
    private String quantityUnit;
    private Float price;
}