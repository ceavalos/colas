package sv.com.freund.rsMHFFactuacionElectronica.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
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
@Table(name = "MNU_PARAMETROS_GENERALES", schema = "MENU")



/*
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "recepcion_factura",
        procedureName = "pkg_mhf_procesos_02.prc_recepcion_factura",
        parameters = {
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tic_cod_gen", type=String.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tic_fecha_emi", type=String.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tic_hora_emi", type=String.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tic_cod_tipod", type=String.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tic_tipo_modelo", type=Integer.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tin_tipo_operacion", type=Integer.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tin_version", type=Integer.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tin_ambiente", type=String.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tic_json", type=String.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tin_nrc_emisor", type=Integer.class),
          @StoredProcedureParameter(mode=ParameterMode.IN, name="tic_ban_estado", type=String.class),
          @StoredProcedureParameter(mode=ParameterMode.OUT, name="toc_respuesta", type=String.class)
    })
})
*/


public class MnuParametrosGenerales implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MnuParametrosGeneralesPK mnuParametrosGeneralesPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "VALOR_PARAMETRO")
    private String valorParametro;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 400)
    @Column(name = "DESCRIPCION_PARAMETRO")
    private String descripcionParametro;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "BAN_ESTADO")
    private String banEstado;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "BAN_TIPO_PARAMETRO")
    private String banTipoParametro;
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
    @Size(max = 1)
    @Column(name = "BAN_RESTRINGIDO")
    private String banRestringido;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "BAN_VALIDAR_CASE")
    private String banValidarCase;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "BAN_TIPO_VALIDACION_CAMPO")
    private String banTipoValidacionCampo;
    @Size(max = 300)
    @Column(name = "VALIDA_VALORES_CAMPO")
    private String validaValoresCampo;
    @Size(max = 1000)
    @Column(name = "VALOR_PARAMETRO_12C")
    private String valorParametro12c;
    @JoinColumn(name = "COD_MENU", referencedColumnName = "COD_MENU", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Menus menus;
    @OneToMany(mappedBy = "mnuParametrosGenerales", fetch = FetchType.LAZY)
    private Set<MnuParametrosGenerales> mnuParametrosGeneralesSet;
    @JoinColumns({
        @JoinColumn(name = "COD_MENU_PADRE", referencedColumnName = "COD_MENU")
        , @JoinColumn(name = "NOMBRE_PARAMETRO_PADRE", referencedColumnName = "NOMBRE_PARAMETRO")})
    @ManyToOne(fetch = FetchType.LAZY)
    private MnuParametrosGenerales mnuParametrosGenerales;

    public MnuParametrosGenerales(MnuParametrosGeneralesPK mnuParametrosGeneralesPK) {
        this.mnuParametrosGeneralesPK = mnuParametrosGeneralesPK;
    }

    @XmlTransient
    public Set<MnuParametrosGenerales> getMnuParametrosGeneralesSet() {
        return mnuParametrosGeneralesSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mnuParametrosGeneralesPK != null ? mnuParametrosGeneralesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MnuParametrosGenerales)) {
            return false;
        }
        MnuParametrosGenerales other = (MnuParametrosGenerales) object;
        if ((this.mnuParametrosGeneralesPK == null && other.mnuParametrosGeneralesPK != null) || (this.mnuParametrosGeneralesPK != null && !this.mnuParametrosGeneralesPK.equals(other.mnuParametrosGeneralesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.com.freund.rsTrsBeetrackLastmile.entity.MnuParametrosGenerales[ mnuParametrosGeneralesPK=" + mnuParametrosGeneralesPK + " ]";
    }
    
}
