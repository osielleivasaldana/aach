package cl.aach.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

/**
 * Utilidad para recuperar datos GPS a partir de un token mediante un servicio SOAP.
 */
public class RecuperarGps {

    // ==============================
    // Constantes
    // ==============================
    private static final String URL_SOAP = "https://sgps.aachtest.cl/wsgps/";

    // ==============================
    // Métodos Públicos
    // ==============================

    /**
     * Recupera los datos GPS a partir de un token.
     *
     * @param token El token que se enviará en la solicitud SOAP.
     * @return Un arreglo de String con los valores de IdSGPS y DescripcionEstado.
     * @throws Exception Si ocurre algún problema durante el procesamiento.
     */
    public static String[] recuperarGps(String token) throws Exception {
        // Crear conexión SOAP
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Construir la solicitud SOAP
        SOAPMessage soapMessage = createSOAPRequest(token);

        // Enviar la solicitud y recibir la respuesta
        SOAPMessage soapResponse = soapConnection.call(soapMessage, URL_SOAP);

        // Opcional: Cerrar la conexión SOAP (según las recomendaciones del proveedor)
        soapConnection.close();

        // Convertir la respuesta SOAP a un String legible
        String responseString = soapMessageToString(soapResponse);

        // Procesar el XML de respuesta y extraer los datos
        return parseSOAPResponse(responseString);
    }

    // ==============================
    // Métodos Privados
    // ==============================

    /**
     * Construye el mensaje SOAP para enviar la solicitud al servicio RecuperarSGPS.
     *
     * @param token El token que se enviará.
     * @return El mensaje SOAP construido.
     * @throws Exception Si ocurre algún error al construir el mensaje.
     */
    private static SOAPMessage createSOAPRequest(String token) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        // Crear el envelope y agregar el namespace
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("wsg", "https://sgps.aachtest.cl/wsgps/");

        // Construir el header con credenciales
        SOAPHeader header = envelope.getHeader();
        SOAPElement credencialesUsuario = header.addChildElement("CredencialesUsuario", "wsg");
        credencialesUsuario.addChildElement("UsuarioNombre", "wsg").addTextNode("GPS04007961");
        credencialesUsuario.addChildElement("UsuarioClave", "wsg").addTextNode("Orellana1");

        // Construir el body de la solicitud
        SOAPBody body = envelope.getBody();
        SOAPElement recuperarSGPS = body.addChildElement("RecuperarSGPS", "wsg");
        recuperarSGPS.addChildElement("tipo", "wsg").addTextNode("T");
        recuperarSGPS.addChildElement("token", "wsg").addTextNode(token);
        recuperarSGPS.addChildElement("idtransaccion1", "wsg"); // Elemento vacío
        recuperarSGPS.addChildElement("idtransaccion2", "wsg"); // Elemento vacío

        soapMessage.saveChanges();
        return soapMessage;
    }

    /**
     * Convierte un mensaje SOAP en una representación String en UTF-8.
     *
     * @param soapMessage El mensaje SOAP a convertir.
     * @return La representación en String del mensaje.
     * @throws Exception Si ocurre algún error durante la conversión.
     */
    private static String soapMessageToString(SOAPMessage soapMessage) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(outputStream);
        return outputStream.toString("UTF-8");
    }

    /**
     * Procesa la respuesta SOAP y extrae los valores de IdSGPS y DescripcionEstado.
     *
     * @param soapResponse La respuesta SOAP en formato String.
     * @return Un arreglo de String con IdSGPS y DescripcionEstado.
     * @throws Exception Si ocurre algún error durante el procesamiento.
     */
    private static String[] parseSOAPResponse(String soapResponse) throws Exception {
        // Convertir la respuesta en un documento XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(soapResponse.getBytes()));

        // Extraer valores utilizando etiquetas específicas
        NodeList idSGPSNodes = document.getElementsByTagName("IdSGPS");
        NodeList descripcionEstadoNodes = document.getElementsByTagName("DescripcionEstado");

        String idSGPS = (idSGPSNodes.getLength() > 0) ? idSGPSNodes.item(0).getTextContent() : "No disponible";
        String descripcionEstado = (descripcionEstadoNodes.getLength() > 0) ? descripcionEstadoNodes.item(0).getTextContent() : "No disponible";

        return new String[]{idSGPS, descripcionEstado};
    }
}
