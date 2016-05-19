package webapp.jsf;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.sql.DataSource;

@Named
@RequestScoped
public class AdherentRepository {
	@Resource(name = "bdd-javaee")
	private DataSource ds;
	
	private Adherent adherent = new Adherent();

	public AdherentRepository() {		
	}
	
	public Adherent getAdherent(){
		return this.adherent;
	}

	public Adherent create(String email, String password, String confirmedPassword, String webSite, boolean accepted) throws SQLException {
		Adherent s = new Adherent();
		s.setEmail(email);
		s.setPassword(password);
		s.setCreationDate(new Date());
		
		insert(s);
		return s;
	}
	
	public List<Adherent> getAll() throws SQLException {
		List<Adherent> adherents = new ArrayList<>();
		
		try (Statement stmt = ds.getConnection().createStatement();
			 ResultSet rs = stmt.executeQuery("select email, siteWeb, dateAdhesion from Adherent")) {
			while(rs.next()) {
				adherents.add(readSubscription(rs));
			}
		}
		return adherents;
	}

	private Adherent readSubscription(ResultSet rs) throws SQLException {
		Adherent s = new Adherent();
		s.setEmail(rs.getString("email"));
		String website = rs.getString("siteWeb");
		if (! rs.wasNull()) {
			try {
				s.setWebsite(new URL(website));
			} catch (MalformedURLException e) {
				// TODO on devrait rapporter un problème plus proprement
				e.printStackTrace();
			}
		}
		//s.setCreationDate(rs.getDate("dateAdhesion"));
		return s;
	}

	private void insert(Adherent s) throws SQLException {
		String sql = "insert into Adherent (email, motDePasse, siteWeb, dateAdhesion) values(?, SHA1(?), ?, ?)";
		try(PreparedStatement stmt = ds.getConnection().prepareStatement(sql)) {
			stmt.setString(1, s.getEmail());
			stmt.setString(2, s.getPassword());
			if (s.getWebsite() == null) {
				stmt.setNull(3, Types.VARCHAR);
			}
			else {
				stmt.setString(3, s.getWebsite().toString());
			}
			stmt.setDate(4, new java.sql.Date(s.getCreationDate().getTime()));
			stmt.executeUpdate();
		}
	}

}
