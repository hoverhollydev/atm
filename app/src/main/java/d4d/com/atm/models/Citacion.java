package d4d.com.atm.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JuanPablo on 13/08/2016.
 */
public class Citacion{

    String nombre_agente="";
    String tipo_identificacion="";
    String identificacion="";
    String placa="";
    String cod_agente="";
    String articulo="";
    String numeral="";
    String fecha="";
    String direccion="";
    String zona="";
    String provincia="";
    String localidad="";
    String distrito="";
    String circuito="";
    String subCircuito="";
    String institucion="";
    String observacion="";
    String numBoleta="";
    String tipoLicencia="";
    String numCitacion="";
    String descripcion="";
    String longitud="";
    String latitud="";
    String imagen="";
    String imagen2="";
    String transmision="";
    String precision="";
    boolean notifica=false;

    public Citacion(String nombre_agente, String tipo_identificacion, String identificacion, String placa, String cod_agente,
                    String articulo, String numeral, String fecha, String direccion, String zona, String provincia,
                    String localidad, String distrito, String circuito, String subCircuito, String institucion,
                    String observacion, String numBoleta, String tipoLicencia, String numCitacion,
                    String descripcion, String latitud, String longitud, String imagen, String imagen2,
                    String transmision, String precision, boolean notifica){

        this.nombre_agente = nombre_agente;
        this.tipo_identificacion = tipo_identificacion;
        this.identificacion = identificacion;
        this.placa = placa;
        this.cod_agente = cod_agente;
        this.articulo = articulo;
        this.numeral = numeral;
        this.fecha = fecha;
        this.direccion = direccion;
        this.zona = zona;
        this.provincia = provincia;
        this.localidad = localidad;
        this.distrito = distrito;
        this.circuito = circuito;
        this.subCircuito = subCircuito;
        this.institucion = institucion;
        this.observacion = observacion;
        this.numBoleta = numBoleta;
        this.tipoLicencia = tipoLicencia;
        this.numCitacion = numCitacion;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.imagen = imagen;
        this.imagen2 = imagen2;
        this.transmision = transmision;
        this.precision = precision;
        this.notifica = notifica;
    }

    public String getNombre_agente() {
        return nombre_agente;
    }

    public void setNombre_agente(String nombre_agente) {
        this.nombre_agente = nombre_agente;
    }

    public String getTipo_identificacion() {
        return tipo_identificacion;
    }

    public void setTipo_identificacion(String tipo_identificacion) {
        this.tipo_identificacion = tipo_identificacion;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCod_agente() {
        return cod_agente;
    }

    public void setCod_agente(String cod_agente) {
        this.cod_agente = cod_agente;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getNumeral() {
        return numeral;
    }

    public void setNumeral(String numeral) {
        this.numeral = numeral;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getCircuito() {
        return circuito;
    }

    public void setCircuito(String circuito) {
        this.circuito = circuito;
    }

    public String getSubCircuito() {
        return subCircuito;
    }

    public void setSubCircuito(String subCircuito) {
        this.subCircuito = subCircuito;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getNumBoleta() {
        return numBoleta;
    }

    public void setNumBoleta(String numBoleta) {
        this.numBoleta = numBoleta;
    }

    public String getTipoLicencia() {
        return tipoLicencia;
    }

    public void setTipoLicencia(String tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public String getNumCitacion() {
        return numCitacion;
    }

    public void setNumCitacion(String numCitacion) {
        this.numCitacion = numCitacion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getImagen2() {
        return imagen2;
    }

    public void setImagen2(String imagen2) {
        this.imagen2 = imagen2;
    }

    public String getTransmision() {
        return transmision;
    }

    public void setTransmision(String transmision) {
        this.transmision = transmision;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public boolean getNotifica() {
        return notifica;
    }

    public void setNotifica(boolean notifica) {
        this.notifica = notifica;
    }

    public JSONObject getJsonCitacion(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("Usuario", this.nombre_agente);
            jsonObject.accumulate("Tipo_Iden", this.tipo_identificacion);
            jsonObject.accumulate("Identificacion", this.identificacion);
            jsonObject.accumulate("Placa", this.placa);
            jsonObject.accumulate("AgenteTransito", this.cod_agente);
            jsonObject.accumulate("Articulo", this.articulo);
            jsonObject.accumulate("Literal", this.numeral);
            jsonObject.accumulate("Fecha", this.fecha);
            jsonObject.accumulate("LugarInfraccion", this.direccion);
            jsonObject.accumulate("Zona", this.zona);
            jsonObject.accumulate("Provincia", this.provincia);
            jsonObject.accumulate("Localidad", this.localidad);
            jsonObject.accumulate("Distrito", this.distrito);
            jsonObject.accumulate("Circuito", this.circuito);
            jsonObject.accumulate("SubCircuito", this.subCircuito);
            jsonObject.accumulate("Institucion", this.institucion);
            jsonObject.accumulate("Observacion", this.observacion);
            jsonObject.accumulate("Boleta", this.numBoleta);
            jsonObject.accumulate("TipoLicencia", this.tipoLicencia);
            jsonObject.accumulate("NumCitacion", this.numCitacion);
            jsonObject.accumulate("Descripcion", this.descripcion);
            jsonObject.accumulate("Latitud", this.latitud);
            jsonObject.accumulate("Longitud", this.longitud);
            jsonObject.accumulate("transmision", transmision);
            jsonObject.accumulate("precision", precision);
            jsonObject.accumulate("notifica", notifica);
            //Log.i("CITACION",jsonObject.toString());
            jsonObject.accumulate("Imagen", imagen);
            jsonObject.accumulate("Imagen2", imagen2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
