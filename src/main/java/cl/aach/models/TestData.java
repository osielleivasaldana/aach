package cl.aach.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestData {
    public Credentials credentials;
    public Sissoap sissoap;
    public Sisgen sisgen;
    public Esoap esoap;
    public Srci srci;
    public Sisvf sisvf;
    public Reunion reunion; // Incluida la clase Reunion
    public Emanager emanager;
    public Scap scap;
    public Scig scig;
    public Sgm sgm;

    public static class Credentials {
        public PortalUser portalUser;
        public PortaAdmin portalAdmin;
        public Emanager emanager;
        public Sgu sgu;

            public static class PortalUser {
                public String user;
                public String password;
            }
            public static class PortaAdmin {
                public String user;
                public String password;
            }
            public static class Emanager {
                public String user;
                public String password;
            }
            public static class Sgu {
                public String user;
                public String password;
            }
    }

    public static class Sissoap {
        public Patente patente;
        public Rut rut;
            public static class  Patente{
                public String patente;
                public String siniestro;
            }
            public static class Rut{
                public String rut;
                public String compania;
            }

    }

    public static class Sisgen {
        public String rut;
        public String patenteA;
        public String patenteB;
        public String resultadoRut;
        public String resultadoPatente;
    }

    public static class Esoap {
        public Poliza poliza;
        public Patente patente;

        public static class Poliza{
            public String poliza;
            public String patente;
        }
        public static class Patente{
            public String patenteA;
            public String patenteB;
            public String poliza;
        }
    }

    public static class Scap{
        public String fecInicioConsulta;
        public String resultadoEsperado;
    }

    public static class Scig{
        public String fecInicioConsulta;
        public String resultadoEsperado;
    }

    public static class Srci {
        public String rut;
        public String patente;
    }

    public static class Sgm{
        public String carpeta;
        public String libro;
        public String referencia;
        public String responsableTecnico;
        public String responsableTecnicoFinal;
        public String resultadoEsperado;
    }

    public static class Sisvf {
        public String rut;
        public String apellidoPaterno;
        public String apellidoMaterno;
        public String nombres;
        public String fecdef;
        public String fecnac;
    }

    /**
     * Clase Reunion con documentos y tipo.
     */
    public static class Reunion {
        public String comite;
        public String asunto;
        public String ubicacion;
        public String horaInicio;
        public String horaTermino;
        public String desc;
        public Documentos documentos; // Clase anidada para manejar los documentos

        public static class Documentos {
            public String comite;
            public String reunion;
            public String documento;
            public Tipo tipo;
            public String autor;

            public static class Tipo {
                public String doc1;
                public String doc2;
                public String doc3;
                public String doc4;
            }
        }
    }
    public static class Emanager {
        public  Archivo archivo;
        public Normativa normativa;

        public static class Archivo {
            public String titulo;
            public String desc;
            public String materia;
            public String prensa;
            public String archivo;
        }
        public static class Normativa{
            public String nombre;
            public String materia;
            public String archivo;
            public  String resultado;
        }
    }

    /**
     * Utilidad para trabajar con fechas.
     */
    public static class DateUtils {
        public static String convertToAaaammdd(String inputDate) {
            // Define el formato de entrada y el formato deseado
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            // Parsear y formatear la fecha
            LocalDate date = LocalDate.parse(inputDate, inputFormatter);
            return date.format(outputFormatter);
        }
    }
    }

