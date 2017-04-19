package tools;

import java.io.File;
import java.sql.SQLException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import domains.Domains;
import domains.Fields;
import domains.Fields.Field;
import domains.Machinery;
import domains.Machinery.Machine;
import logginig.Logger;

public class ExportImport {
	private static Logger logger = Logger.getLogger(ExportImport.class);

	public static void exportXML(File file) throws JAXBException {
		try{
			Marshaller jaxbMarshaller = JAXBContext
					.newInstance(Domains.class)
					.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(new Domains(Fields.getFields(), Machinery.getMachinery()), file);
		}catch(JAXBException e){
			logger.info(e.getCause().getMessage());
			throw e;
		}
	}
	
	public static void importXML(File file) throws JAXBException, SQLException {	
		Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(Domains.class).createUnmarshaller();
		Domains domains = (Domains) jaxbUnmarshaller.unmarshal(file);
		
		if(domains.machines != null){
			
			logger.info("\tImporting Machines");
			for(Machine m : domains.machines){
				logger.info("\t\tImporting " + m);
				m.save();
			}
		}
		
		if(domains.fields != null){

			logger.info("\tImporting Fields");
			for(Field f : domains.fields){
				if(f.points == null){
					logger.info("Field " + f + " has no points. Skipping");
					continue;
				};
				logger.info("\t\tImporting " + f);
				f.save();				
			}
		}
	}
}
