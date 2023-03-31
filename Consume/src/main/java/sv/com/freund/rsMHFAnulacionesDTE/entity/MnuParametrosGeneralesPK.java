package sv.com.freund.rsMHFAnulacionesDTE.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author sault
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MnuParametrosGeneralesPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "COD_MENU")
    private String codMenu;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "NOMBRE_PARAMETRO")
    private String nombreParametro;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codMenu != null ? codMenu.hashCode() : 0);
        hash += (nombreParametro != null ? nombreParametro.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MnuParametrosGeneralesPK)) {
            return false;
        }
        MnuParametrosGeneralesPK other = (MnuParametrosGeneralesPK) object;
        if ((this.codMenu == null && other.codMenu != null) || (this.codMenu != null && !this.codMenu.equals(other.codMenu))) {
            return false;
        }
        if ((this.nombreParametro == null && other.nombreParametro != null) || (this.nombreParametro != null && !this.nombreParametro.equals(other.nombreParametro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.com.freund.rsTrsBeetrackLastmile.entity.MnuParametrosGeneralesPK[ codMenu=" + codMenu + ", nombreParametro=" + nombreParametro + " ]";
    }
    
}
