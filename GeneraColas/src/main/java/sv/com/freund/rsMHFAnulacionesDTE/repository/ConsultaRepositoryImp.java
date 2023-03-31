/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.com.freund.rsMHFAnulacionesDTE.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.com.freund.rsMHFAnulacionesDTE.entity.MnuParametrosGenerales;
import sv.com.freund.rsMHFAnulacionesDTE.entity.MnuParametrosGeneralesPK;


/**
 *
 * @author Sault
 */
@Repository
public interface ConsultaRepositoryImp extends JpaRepository<MnuParametrosGenerales, MnuParametrosGeneralesPK> {

    /**
     * Funcion para obtener valor del parametros de base de datos
     *
     * @param parametro
     * @param codMenuSistema
     * @return string con el valor del parametro
     */

    @Query(
            nativeQuery = true,
            value = "select PKG_FRE_FUNCIONES_03.fnc_parametros_wl_v2 (?1, ?2) from dual")
    String findParametrosWl(String codMenuSistema, String parametro);

    @Query(
            nativeQuery = true,
            value = "select PKG_MNU_PARAM_SIS_GET_01.fnc_getchar(?1, ?2, ?3) from dual")
    String findApiTokenDokmee(String param1, String param2, String param3);

    @Query(
            nativeQuery = true,
            value = "SELECT pkg_fre_dokmee_jsondata.fnc_fre_dokmee_configuracion(?1) from dual")
    String findAutUser(String param1);

    @Query(
            nativeQuery = true,
            value = "SELECT pkg_fre_dokmee_jsondata.fnc_fre_dokmee_jsondata(?1) from dual")
    String findJsonDataDokmee(String param1);

    @Query(
            nativeQuery = true,
            value = "SELECT pkg_fre_dokmee_jsondata.fnc_fre_dokmee_gab_config(?1) from dual")
    String findGabineteID(String param1);

    @Query(
            nativeQuery = true,
            value = "select pkg_fre_usuarios_acceso.fnc_buscar_user_passwd(?1) from dual")
    String fnc_buscar_user_passwd(String parametro);

    /*
    @Procedure(name = "recepcion_factura")
    String procesar_dte(@Param("tic_cod_gen") String tic_cod_gen, 
                        @Param("tic_fecha_emi") String tic_fecha_emi,
                        @Param("tic_hora_emi") String tic_hora_emi,
                        @Param("tic_cod_tipod") String tic_cod_tipod,
                        @Param("tic_tipo_modelo") Integer tic_tipo_modelo,
                        @Param("tin_tipo_operacion") Integer tin_tipo_operacion,
                        @Param("tin_version") Integer tin_version,
                        @Param("tin_ambiente") String tin_ambiente,
                        @Param("tic_json") String tic_json,
                        @Param("tin_nrc_emisor") Integer tin_nrc_emisor,
                        @Param("tic_ban_estado") String tic_ban_estado
                        );*/
}
