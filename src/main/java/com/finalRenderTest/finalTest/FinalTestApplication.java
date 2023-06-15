package com.finalRenderTest.finalTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@SpringBootApplication
@RestController
@RequestMapping("")
public class FinalTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalTestApplication.class, args);
	}

	@GetMapping("/test")
	public String test1() {
		return "123";
	}

	@GetMapping("/works")
	public String test2() {
		return "TESTTEST";
	}

	@GetMapping("")
	public String test() {
		try {
			StringBuilder stringBuilder = new StringBuilder("<html>");
			stringBuilder.append("<head>");
			stringBuilder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">");
			stringBuilder.append("</head>");

			stringBuilder.append("<h4>Currently using " + com.databaseviewer.dbviewerapp.DatabaseConnector.getDatabaseName() + " database.</h4>\n");
			HashSet<String> hashSet = com.databaseviewer.dbviewerapp.DatabaseConnector.getDatabaseInfo();

			stringBuilder.append("<form id=\"myForm\">");
			stringBuilder.append("<select id=\"tableNameSelect\">");
			hashSet.forEach(option -> stringBuilder.append("<option>").append(option).append("</option>"));
			stringBuilder.append("</select>");
			stringBuilder.append("</form>");

			stringBuilder.append("<button class=\"custom-button\" type=\"button\" onclick=\"gotoTable()\">Pokaż zawartość tabeli</button>");

			// stringBuilder.append("<div id=\"displayDiv\"></div>");

			stringBuilder.append("<script>");
			stringBuilder.append("function gotoTable() {");
			stringBuilder.append("	var tableName = document.getElementById('tableNameSelect').value;");
			stringBuilder.append("	window.location.href = '/tables/' + tableName;");
			stringBuilder.append("}");
			stringBuilder.append("</script>");

			return stringBuilder.toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@GetMapping("/tables/{tableName}")
	public String getTableContent(@PathVariable String tableName) {
		StringBuilder stringBuilder = new StringBuilder("<html>");
		stringBuilder.append("<head>");
		stringBuilder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">");
		stringBuilder.append("</head>");
		String content = com.databaseviewer.dbviewerapp.DatabaseConnector.getTableContent(tableName);
		if (content == null) {
			stringBuilder.append("<h4>Table \"").append(tableName).append("\" does not exists</h4>");
			stringBuilder.append("</html>");
			return stringBuilder.toString();
		}

		stringBuilder.append(content);

		stringBuilder.append("<br><br>");
		stringBuilder.append("<button class=\"custom-button\" type=\"button\" onclick=\"goToRoot()\">goToRoot</button>");

		stringBuilder.append("<script>");
		stringBuilder.append("function goToRoot() {");
		stringBuilder.append("	window.location.href = '/';");
		stringBuilder.append("}");
		stringBuilder.append("</script>");

		stringBuilder.append("<h2>Alter database content</h2>");
		// stringBuilder.append("</html>");

		stringBuilder.append("<button class=\"custom-button\" type=\"button\" onclick=\"alterDatabase()\">Remove by unique id</button>");

		stringBuilder.append("<br><br>");
		stringBuilder.append("<input type=\"text\" id=\"myTextField\" required>");

		stringBuilder.append("<script>");
		stringBuilder.append("function alterDatabase() {");
		stringBuilder.append("    var userInput = document.getElementById('myTextField').value;");
		stringBuilder.append("    var payload = {");
		stringBuilder.append("        tableName: '").append(tableName).append("',");
		stringBuilder.append("        pass: 'safety',");
		stringBuilder.append("        ID: parseInt(userInput)");
		stringBuilder.append("    };");
		stringBuilder.append("    fetch('/remove', {");
		stringBuilder.append("        method: 'POST',");
		stringBuilder.append("        headers: {");
		stringBuilder.append("            'Content-Type': 'application/json'");
		stringBuilder.append("        },");
		stringBuilder.append("        body: JSON.stringify(payload)");
		stringBuilder.append("    })");
		stringBuilder.append("    .then(response => response.text())");
		stringBuilder.append("    .then(data => {");
		stringBuilder.append("        console.log(data);");
		stringBuilder.append("    })");
		stringBuilder.append("    .catch(error => {");
		stringBuilder.append("        console.error('Error:', error);");
		stringBuilder.append("    });");
		stringBuilder.append("}");
		stringBuilder.append("</script>");

		stringBuilder.append("<br></br>");
		stringBuilder.append("<h3>Alter database row:</h3>");

		stringBuilder.append("<h3>Field Name:</h3>");
		stringBuilder.append("<input type=\"text\" id=\"FieldName\">");

		stringBuilder.append("<h3>New Field Name:</h3>");
		stringBuilder.append("<input type=\"text\" id=\"NewValue\">");

		stringBuilder.append("<h3>Row's ID:</h3>");
		stringBuilder.append("<input type=\"text\" id=\"RowID\">");
		stringBuilder.append("<br></br>");
		stringBuilder.append("<button class=\"custom-button\" type=\"button\" onclick=\"alterTableEntry()\">Alter table</button>");

		stringBuilder.append("<script>");
		stringBuilder.append("function alterTableEntry() {");
		stringBuilder.append("    var fieldName = document.getElementById('FieldName').value;");
		stringBuilder.append("    var newValue = document.getElementById('NewValue').value;");
		stringBuilder.append("    var rowID = document.getElementById('RowID').value;");

		stringBuilder.append("    var payload = {");
		stringBuilder.append("        pass: 'safety',");
		stringBuilder.append("        tableName: '").append(tableName).append("',");
		stringBuilder.append("        columnName: document.getElementById('FieldName').value,");
		stringBuilder.append("        newValue: newValue,");
		stringBuilder.append("        ID: rowID");
		stringBuilder.append("    };");

		stringBuilder.append("    fetch('/alter', {");
		stringBuilder.append("        method: 'POST',");
		stringBuilder.append("        headers: {");
		stringBuilder.append("            'Content-Type': 'application/json'");
		stringBuilder.append("        },");
		stringBuilder.append("        body: JSON.stringify(payload)");
		stringBuilder.append("    })");
		stringBuilder.append("    .then(response => response.text())");
		stringBuilder.append("    .then(data => {");
		stringBuilder.append("        console.log(data);");
		stringBuilder.append("    })");
		stringBuilder.append("    .catch(error => {");
		stringBuilder.append("        console.error('Error:', error);");
		stringBuilder.append("    });");
		stringBuilder.append("}");
		stringBuilder.append("</script>");

		stringBuilder.append("</html>");

		return stringBuilder.toString();
	}

	@PostMapping("/remove")
	public String removeTable(@RequestBody com.databaseviewer.dbviewerapp.RemoveRequest requestData) {
		String tableName = requestData.tableName();
		String pass = requestData.pass();
		int number = requestData.ID();
		if (!pass.equals("safety"))
			return "Failed to authenticate user";
		try {
			com.databaseviewer.dbviewerapp.DatabaseConnector.removeEntry(tableName, number);
		}
		catch (Exception e) {
			e.printStackTrace();
			return "Failed to remove an entry";
		}

		System.out.println("String: " + tableName + " String2: " + pass + ", Number: " + number);
		return "String: " + tableName + " String2: " + pass + ", Number: " + number;
	}

	@PostMapping("/alter")
	public String alterTable(@RequestBody com.databaseviewer.dbviewerapp.AlterRequest alterRequest) {
		String pass = alterRequest.pass();
		String tableName = alterRequest.tableName();
		String columnName = alterRequest.columnName();
		String newValue = alterRequest.newValue();
		int id = alterRequest.ID();
//		return pass + " " + tableName + " " + columnName + " " + newValue + " " + id;
		System.out.println(pass + " " + tableName + " " + columnName + " " + newValue + " " + id);
		if (!pass.equals("safety"))
			return "Failed to authenticate user";

		try {
			com.databaseviewer.dbviewerapp.DatabaseConnector.alterEntry(tableName, columnName, newValue, id);
		}
		catch (Exception e) {
			e.printStackTrace();
			return "Failed to update";
		}
		return "Ok";
	}

}
