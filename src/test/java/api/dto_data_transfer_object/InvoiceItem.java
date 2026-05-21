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
public class InvoiceItem {
    private String name;
    private Float price;
    private Float quantity;
    // Measurement unit of the quantity
    @SerializedName("quantity_unit")
    private String quantityUnit;
}
