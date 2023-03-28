package d4d.com.atm.utils;

/**
 *
 * @author Ju@n P@blo LeoN

 */
public class Validacion {
    private String cadena;
    public Validacion(String cad)
    {
        cadena=cad;
    }
    public String obtenerCadena()
    {
        return cadena;
    }
    public void establecerCadena(String c)
    {
        cadena=c;
    }
    public boolean vacio()
    {
        boolean respuesta=false;
        String aux;
        aux=cadena.trim();
        if(aux.equals(""))
            respuesta=true;
        else
        {
            for(int i=0;i<=cadena.length();i++)
                aux+=" ";
            if (cadena.equals(aux))
                respuesta=true;
        }
        return respuesta;
    }
    public boolean solamenteLetras()
    {
        boolean respuesta=false;
        String aux;
            int longitud = cadena.length();
        if (longitud == 0 )
            respuesta = false;
        if (longitud > 0 )
        {
            for(int i=0;i<cadena.length();i++)
               if (cadena.charAt(i)>='a' && cadena.charAt(i)<='z' || cadena.charAt(i)>='A' && cadena.charAt(i)<='Z' || cadena.charAt(i)==' ' || cadena.charAt(i)=='ú' || cadena.charAt(i)=='ó'|| cadena.charAt(i)=='í'|| cadena.charAt(i)=='é'|| cadena.charAt(i)=='á' || cadena.charAt(i)=='Ú' || cadena.charAt(i)=='Ó'|| cadena.charAt(i)=='Í'|| cadena.charAt(i)=='É'|| cadena.charAt(i)=='Á'|| cadena.charAt(i)=='ñ'|| cadena.charAt(i)=='Ñ')
                    respuesta=true;
               else
               {
                   respuesta=false;
                   break;
               }
        }
        return respuesta;
    }
    public boolean solamenteLetrasMayusculas()
    {
        boolean respuesta=false;
        String aux=cadena;
        respuesta=solamenteLetras();
        if (respuesta==true)
            if (aux.toUpperCase().equals(cadena))
                respuesta=true;
            else
                respuesta=false;
        else
            respuesta=false;
        return respuesta;
    }

    public boolean numerosLetras()
    {
        boolean respuesta=false;
        String aux;
        int longitud = cadena.length();
        if (longitud == 0 )
            respuesta = false;
        if (longitud > 0 )
        {
            for(int i=0;i<cadena.length();i++)
               if (cadena.charAt(i)>='0'&& cadena.charAt(i)<='9'||cadena.charAt(i)>='a' && cadena.charAt(i)<='z' || cadena.charAt(i)>='A' && cadena.charAt(i)<='Z' || cadena.charAt(i)=='ñ'|| cadena.charAt(i)=='Ñ')
                    respuesta=true;
               else
               {
                   respuesta=false;
                   break;
               }
        }
        return respuesta;
    }
    public boolean numerosLetrasEspacios()
    {
        boolean respuesta=false;
        String aux;
        int longitud = cadena.length();
        if (longitud == 0 )
            respuesta = false;
        if (longitud > 0 )
        {
            for(int i=0;i<cadena.length();i++)
               if (cadena.charAt(i)>='a' && cadena.charAt(i)<='z' || cadena.charAt(i)>='A' && cadena.charAt(i)<='Z' || cadena.charAt(i)>='0'&& cadena.charAt(i)<='9'||cadena.charAt(i)==' ' || cadena.charAt(i)=='ú' || cadena.charAt(i)=='ó'|| cadena.charAt(i)=='í'|| cadena.charAt(i)=='é'|| cadena.charAt(i)=='á' || cadena.charAt(i)=='Ú' || cadena.charAt(i)=='Ó'|| cadena.charAt(i)=='Í'|| cadena.charAt(i)=='É'|| cadena.charAt(i)=='Á'|| cadena.charAt(i)=='ñ'|| cadena.charAt(i)=='Ñ')
                    respuesta=true;
               else
               {
                   respuesta=false;
                   break;
               }
        }
        return respuesta;
    }

    public boolean numeroEntero()
    {
        boolean respuesta;
        int numero;
        String sinSigno=cadena;
        try
        {
            if(cadena.charAt(0)=='+')
                establecerCadena(sinSigno.replace("+", ""));
            numero= Integer.parseInt(cadena);
            respuesta=true;
        }catch(Exception excepcion)
        {
            respuesta=false;
        }
        return respuesta;
    }

    public boolean placaAuto()
    {
        boolean respuesta=false,bandera1=false,bandera2=false,bandera3=false;
        String auxpro=cadena;
        String aux,aux2=cadena,aux3=cadena;
        bandera1=numerosLetras();
        if(bandera1==true) {
            if (auxpro.length() == 7) {
                aux = auxpro.substring(0, 3);
                establecerCadena(aux);
                bandera1 = solamenteLetrasMayusculas();
                aux = aux2.substring(3, 7);
                establecerCadena(aux);
                bandera2 = numeroEntero();
                //if(aux2.charAt(3)=='-')
                // bandera3=true;
                if ((bandera1 == true) && (bandera2 == true))
                    respuesta = true;
            } else if (auxpro.length() == 6) {
                aux = auxpro.substring(0, 2);
                establecerCadena(aux);
                bandera1 = solamenteLetrasMayusculas();
                aux = aux2.substring(2, 5);
                establecerCadena(aux);
                bandera2 = numeroEntero();
                aux = aux3.substring(5);
                establecerCadena(aux);
                bandera3 = solamenteLetrasMayusculas();
                if ((bandera1 == true) && (bandera2 == true) && (bandera3 == true))
                    respuesta = true;
            } else if (auxpro.length() == 5) {
                aux = auxpro.substring(0, 2);
                establecerCadena(aux);
                bandera1 = solamenteLetrasMayusculas();
                aux = aux2.substring(2, 5);
                establecerCadena(aux);
                bandera2 = numeroEntero();
                if ((bandera1 == true) && (bandera2 == true))
                    respuesta = true;
            } else {
                respuesta = false;
            }
        }else {
            respuesta = false;
        }
        return respuesta;
    }

    public boolean EAN13()
    {
        boolean respuesta=false;
        int aux1=0,aux2=0;
        if (cadena.length()==13)
        {
            for(int i=12;i>=1;i--)
            {
                if(i%2==0)
                    aux1+= Integer.parseInt(cadena.substring(i-1,i))*3;
                else
                    aux2+= Integer.parseInt(cadena.substring(i-1,i))*1;
            }
            aux2+=aux1+ Integer.parseInt(cadena.substring(12));
            if (aux2%10==0)
                respuesta=true;
            else
                respuesta=false;
        }
        else
            respuesta=false;
        return respuesta;
    }
    public boolean cedulaRUC()
    {
        boolean respuesta=false;
        int verificador,mult=0,residuo,aux2=0,cont=2;
        String aux="";
        int posicion1=3,posicion2=9;
        //if(((cadena.length()==10)||((cadena.length()==13)&&((Integer.parseInt(cadena.substring(10))>=1))&&(Integer.parseInt(cadena.substring(10))<=999)))
        if(((cadena.length()==10)||((cadena.length()==10)&&((Integer.parseInt(cadena.substring(10))>=1))&&(Integer.parseInt(cadena.substring(10))<=999)))
            &&(((cadena.charAt(2)>='0')&&(cadena.charAt(2)<'6'))&&((Integer.parseInt(cadena.substring(0, 2))>=1)&&(Integer.parseInt(cadena.substring(0, 2))<=24))))
        {
                for(int i=1;i<=9;i++)
                {
                    if(i%2!=0){
                        mult=(Integer.parseInt(cadena.substring(i-1,i))*2);
                        if(mult>=10){
                            aux+=mult;
                            mult= Integer.parseInt(aux.substring(0,1))+ Integer.parseInt(aux.substring(1));}
                    }else
                        mult=(Integer.parseInt(cadena.substring(i-1,i))*1);
                    aux2+=mult;
                }
                residuo=aux2%10;
                if (residuo==0)
                    verificador=0;
                else
                    verificador=10-residuo;
                if(cadena.length()==10){
                    if(verificador== Integer.parseInt(cadena.substring(9)))
                        respuesta=true;}
                else{
                    if(verificador== Integer.parseInt(cadena.substring(9,10)))
                        respuesta=true;}
        }else if(((cadena.length()==13)&&((Integer.parseInt(cadena.substring(2,3))==6)||(Integer.parseInt(cadena.substring(2,3))==9)))&&((Integer.parseInt(cadena.substring(10))>=1))&&(Integer.parseInt(cadena.substring(10))<=999))
        {
            if((Integer.parseInt(cadena.substring(0, 2))>=1)&&(Integer.parseInt(cadena.substring(0, 2))<=24))
            {
                if(Integer.parseInt(cadena.substring(2,3))==9)
                {
                    do{
                        if((cont>=2)&&(cont<=4)){
                            mult=(Integer.parseInt(cadena.substring(posicion1-1,posicion1))*cont)+(Integer.parseInt(cadena.substring(posicion2-1,posicion2))*cont);
                            aux2+=mult;
                            cont++;
                            posicion1--;
                            posicion2--;}
                        else{
                            mult= Integer.parseInt(cadena.substring(posicion2-1,posicion2))*cont;
                            aux2+=mult;
                            cont++;
                            posicion2--;}
                    }while(cont<=7);
                    residuo=aux2%11;
                    if (residuo==0)
                        verificador=0;
                    else
                        verificador=11-residuo;
                    if(verificador== Integer.parseInt(cadena.substring(9,10)))
                        respuesta=true;
                }
                else if((Integer.parseInt(cadena.substring(2,3))==6)&&((Integer.parseInt(cadena.substring(9))>=1))&&(Integer.parseInt(cadena.substring(9))<=9999))
                {
                    posicion1=2;
                    posicion2=8;
                    do{
                        if((cont>=2)&&(cont<=3)){
                            mult=(Integer.parseInt(cadena.substring(posicion1-1,posicion1))*cont)+(Integer.parseInt(cadena.substring(posicion2-1,posicion2))*cont);
                            aux2+=mult;
                            cont++;
                            posicion1--;
                            posicion2--;}
                        else{
                            mult= Integer.parseInt(cadena.substring(posicion2-1,posicion2))*cont;
                            aux2+=mult;
                            cont++;
                            posicion2--;}
                    }while(cont<=7);
                    residuo=aux2%11;
                    if (residuo==0)
                        verificador=0;
                    else
                        verificador=11-residuo;
                    if(verificador== Integer.parseInt(cadena.substring(8,9)))
                        respuesta=true;
                }
            }
        }
        return true;//desactivado
    }
}
