package cl.aach.utils;

import javax.xml.soap.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class AccederFormulario {

    private static final String URL_SOAP = "https://sgps.aachtest.cl/wsgps/";

    public static String[] accederFormulario(String rutCia, String idTransaccion1) throws Exception {
        // Crear conexión SOAP
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Construir solicitud SOAP
        SOAPMessage soapMessage = createSOAPRequest(rutCia, idTransaccion1);

        // Enviar la solicitud y recibir respuesta
        SOAPMessage soapResponse = soapConnection.call(soapMessage, URL_SOAP);

        // Convertir la respuesta SOAP a un String legible
        String responseString = soapMessageToString(soapResponse);

        // Procesar el XML de respuesta y extraer los datos
        return parseSOAPResponse(responseString);
    }

    private static SOAPMessage createSOAPRequest(String rutCia, String idTransaccion1) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        // Crear el envelope
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("wsg", "https://sgps.aachtest.cl/wsgps/");

        // Header
        SOAPHeader header = envelope.getHeader();
        SOAPElement credencialesUsuario = header.addChildElement("CredencialesUsuario", "wsg");
        credencialesUsuario.addChildElement("UsuarioNombre", "wsg").addTextNode("GPS04007961");
        credencialesUsuario.addChildElement("UsuarioClave", "wsg").addTextNode("Orellana1");

        // Body
        SOAPBody body = envelope.getBody();
        SOAPElement accederFormulario = body.addChildElement("AccederFormulario", "wsg");
        accederFormulario.addChildElement("rutCia", "wsg").addTextNode(rutCia);
        accederFormulario.addChildElement("rutCV", "wsg"); // Vacío
        accederFormulario.addChildElement("idtransaccion1", "wsg").addTextNode(idTransaccion1);
        accederFormulario.addChildElement("idtransaccion2", "wsg"); // Vacío

        soapMessage.saveChanges();

        return soapMessage;
    }

    private static String soapMessageToString(SOAPMessage soapMessage) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(outputStream);
        return outputStream.toString("UTF-8");
    }

    private static String[] parseSOAPResponse(String soapResponse) throws Exception {
        // Convertir la respuesta en un documento XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(soapResponse.getBytes()));

        // Extraer valores del XML usando etiquetas específicas
        NodeList idSGPSNodes = document.getElementsByTagName("IdSGPS");
        NodeList tokenNodes = document.getElementsByTagName("Token");
        NodeList descripcionEstadoNodes = document.getElementsByTagName("DescripcionEstado");

        String idSGPS = (idSGPSNodes.getLength() > 0) ? idSGPSNodes.item(0).getTextContent() : "No disponible";
        String token = (tokenNodes.getLength() > 0) ? tokenNodes.item(0).getTextContent() : "No disponible";
        String descripcionEstado = (descripcionEstadoNodes.getLength() > 0) ? descripcionEstadoNodes.item(0).getTextContent() : "No disponible";

        return new String[]{idSGPS, token, descripcionEstado};
    }
}
