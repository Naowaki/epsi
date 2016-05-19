package webapp.jsf;
import java.util.ArrayList;
import java.util.List;

public class DBException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final List<String> errors = new ArrayList<String>();
	
	public DBException(){
		
	}
	
	public void addError (String m) {
		errors.add(m);
	}
	
	public List<String> getErrors(){
		return errors;
	}
}
