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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "MENUS", schema = "MENU")
public class Menus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "COD_MENU")
    private String codMenu;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "NOMBRE_MENU")
    private String nombreMenu;
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
    @Size(max = 60)
    @Column(name = "NOMBRE_OBJETO")
    private String nombreObjeto;
    @Size(max = 200)
    @Column(name = "PASO_OBJETO")
    private String pasoObjeto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4000)
    @Column(name = "DESCRIPCION_SISTEMA")
    private String descripcionSistema;
    @Size(max = 30)
    @Column(name = "LOGO_SISTEMA")
    private String logoSistema;
    @Size(max = 400)
    @Column(name = "MANUAL_USUARIO")
    private String manualUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "BAN_FORMA")
    private String banForma;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "BAN_MUESTRA_MENU")
    private String banMuestraMenu;
    @Size(max = 50)
    @Column(name = "ICONO_SISTEMA")
    private String iconoSistema;
    @Size(max = 10)
    @Column(name = "DESCRIPCION_CORTA")
    private String descripcionCorta;
    @Size(max = 1000)
    @Column(name = "NOMBRE_OBJETO_WEB")
    private String nombreObjetoWeb;
    @Size(max = 10)
    @Column(name = "ID_TIPO_LISTA")
    private String idTipoLista;
    @Size(max = 100)
    @Column(name = "RUTA_OBJETOS_FORMS_WEB")
    private String rutaObjetosFormsWeb;
    @Size(max = 500)
    @Column(name = "RUTA_OBJETOS_FORMS_WEB_WIN")
    private String rutaObjetosFormsWebWin;
    @Size(max = 1)
    @Column(name = "BAN_FORMS_WEB")
    private String banFormsWeb;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "menus", fetch = FetchType.LAZY)
    private Set<MnuParametrosGenerales> mnuParametrosGeneralesSet;
    @JoinColumn(name = "CODIGO_AGRUPACION", referencedColumnName = "CODIGO_AGRUPACION")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AgrupacionMenus codigoAgrupacion;

    public Menus(String codMenu) {
        this.codMenu = codMenu;
    }

    @XmlTransient
    public Set<MnuParametrosGenerales> getMnuParametrosGeneralesSet() {
        return mnuParametrosGeneralesSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codMenu != null ? codMenu.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Menus)) {
            return false;
        }
        Menus other = (Menus) object;
        if ((this.codMenu == null && other.codMenu != null) || (this.codMenu != null && !this.codMenu.equals(other.codMenu))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.com.freund.rsTrsBeetrackLastmile.entity.Menus[ codMenu=" + codMenu + " ]";
    }
    
}
