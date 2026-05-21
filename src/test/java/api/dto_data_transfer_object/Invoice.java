package api.dto_data_transfer_object;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    private Integer id;
    @SerializedName("to_name")
    private String toName;
    @SerializedName("to_address")
    private String toAddress;
    @SerializedName("to_egn")
    private String toEgn;
    @SerializedName("to_mol")
    private String toMol;
    @SerializedName("to_bulstat")
    private String toBulstat;
    @SerializedName("to_is_reg_vat")
    private Boolean toIsRegVat;
    @SerializedName("to_vat_number")
    private String toVatNumber;
    // List of items in the invoice
    private List<InvoiceItem> items;
    // Invoice number
    private Integer number;
    // Type of the document
    private String type;
    // Currency of the payment
    @SerializedName("payment_currency")
    private String paymentCurrency;
}
