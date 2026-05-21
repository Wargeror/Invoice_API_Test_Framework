package api.dto_data_transfer_object;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Client
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private String name;
    private String town;
    private String address;
    private String bulstat;
    // VAT registration status of the client
    @SerializedName("is_reg_vat")
    private Boolean isRegVat;
    // VAT number of the client
    @SerializedName("vat_number")
    private String vatNumber;
    private String mol;
    // Person status of the client
    @SerializedName("is_person")
    private Boolean isPerson;

    /**
     * Constructor with name
     * @param name
     */
    public Client(String name) {
        this.name = name;
    }
}
