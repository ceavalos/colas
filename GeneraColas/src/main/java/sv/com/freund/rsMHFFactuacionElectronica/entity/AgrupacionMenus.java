package sv.com.freund.rsMHFFactuacionElectronica.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
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
@Entity
@Table(name = "AGRUPACION_MENUS", catalog = "", schema = "MENU")
public class AgrupacionMenus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "CODIGO_AGRUPACION")
    private Short codigoAgrupacion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "NOMBRE_AGRUPACION")
    private String nombreAgrupacion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "USUARIO_GRA")
    private String usuarioGra;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "USUARIO_MOD")
    private String usuarioMod;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_GRA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGra;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_MOD")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaMod;
    @Size(max = 50)
    @Column(name = "ICONO_GRUPO")
    private String iconoGrupo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codigoAgrupacion", fetch = FetchType.LAZY)
    private Set<Menus> menusSet;

    public AgrupacionMenus(Short codigoAgrupacion) {
        this.codigoAgrupacion = codigoAgrupacion;
    }

    @XmlTransient
    public Set<Menus> getMenusSet() {
        return menusSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigoAgrupacion != null ? codigoAgrupacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AgrupacionMenus)) {
            return false;
        }
        AgrupacionMenus other = (AgrupacionMenus) object;
        if ((this.codigoAgrupacion == null && other.codigoAgrupacion != null) || (this.codigoAgrupacion != null && !this.codigoAgrupacion.equals(other.codigoAgrupacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.com.freund.rsTrsBeetrackLastmile.entity.AgrupacionMenus[ codigoAgrupacion=" + codigoAgrupacion + " ]";
    }
    
}