
package de.freerider.restapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

// doesn't work(!!!) javax -> jakarta
import javax.servlet.http.HttpServletRequest;

//import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.freerider.datamodel.Customer;
import de.freerider.repository.CustomerRepository;

import com.fasterxml.jackson.core.type.TypeReference;

@RestController
class CustomersController implements CustomersAPI {

	//
	@Autowired
	private CustomerRepository customerRepository;
	//
	private final ObjectMapper objectMapper;
	//
	private final HttpServletRequest request;
	//

	/**
	 * Constructor.
	 * 
	 * @param objectMapper entry point to JSON tree for the Jackson library
	 * @param request      HTTP request object
	 */
	public CustomersController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	/**
	 * GET /customers
	 * 
	 * Return JSON Array of customers
	 * 
	 * @return JSON Array of customers
	 */
	@Override
	public ResponseEntity<List<?>> getCustomers() {
		// TODO Auto-generated method stub
		//
		ResponseEntity<List<?>> re = null;
		System.err.println(request.getMethod() + " " + request.getRequestURI());
		try {
			ArrayNode arrayNode = objectMapper.createArrayNode();

			customerRepository.findAll().forEach(c -> {
				StringBuffer sb = new StringBuffer();
				c.getContacts().forEach(contact -> sb.append(sb.length() == 0 ? "" : "; ").append(contact));
				arrayNode.add(objectMapper.createObjectNode().put("id", c.getId()).put("name", c.getLastName())
						.put("first", c.getFirstName()).put("contacts", sb.toString()));
			});
			ObjectReader reader = objectMapper.readerFor(new TypeReference<List<ObjectNode>>() {
			});
			List<String> list = reader.readValue(arrayNode);
			//
			re = new ResponseEntity<List<?>>(list, HttpStatus.OK);

		} catch (IOException e) {
			re = new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return re;
	}

	/**
	 * GET /customers/:id
	 * 
	 * Return JSON Customers
	 * 
	 * @return JSON Customer
	 */
	@Override
	public ResponseEntity<?> getCustomer(long id) {
		// TODO Auto-generated method stub
		System.err.println(request.getMethod() + " " + request.getRequestURI());

		try {
			Customer c = customerRepository.findById(id).get();

			StringBuffer sb = new StringBuffer();
			c.getContacts().forEach(contact -> sb.append(sb.length() == 0 ? "" : "; ").append(contact));
			ObjectNode result = objectMapper.createObjectNode().put("id", c.getId()).put("name", c.getLastName())
					.put("first", c.getFirstName()).put("contacts", sb.toString());
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			ObjectNode result = objectMapper.createObjectNode().put("Error", e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * POST /customers
	 */
	public ResponseEntity<List<?>> postCustomers(Map<String, Object>[] jsonMap)  {
		// TODO Auto-generated method stub
		System.err.println("POST /customers");

		if (jsonMap == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		} else {
			//
			try {
				List<Customer> custList = new ArrayList<>();
				for (Map<String, Object> kvpairs : jsonMap) {
					
					if (!accept(kvpairs).equals(null)) {
						Customer c = accept(kvpairs).get();
						custList.add(c);
					} 
				}
			
				customerRepository.saveAll(custList);
			} catch (IllegalArgumentException e) {
				return new ResponseEntity<>(null, HttpStatus.CONFLICT);
			} catch (NoSuchElementException e) {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(null, HttpStatus.CREATED);
		} 
	}

	/**
	 * PUT /customers
	 */
	@Override
	public ResponseEntity<List<?>> putCustomers(Map<String, Object>[] jsonMap) {
		// TODO Auto-generated method stub

		return null;
	}

	/**
	 * DELETE /customers/:id
	 */
	public ResponseEntity<?> deleteCustomer(long id) {
		// TODO Auto-generated method stub

		System.err.println(request.getMethod() + " " + request.getRequestURI());
		try {
			customerRepository.deleteById(id);
			return new ResponseEntity<>(null, HttpStatus.ACCEPTED); // status 202
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // status 400
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // status 404
		}
	}

	/**
	 * Convert JSON-Value to Optional<Customer>
	 */
	private Optional<Customer> accept(Map<String, Object> kvpairs) {
		// ...for( Map<String, Object> kvpairs : jsonMap ) {
		Customer customer = new Customer();
		if ( kvpairs.containsKey("name") && kvpairs.containsKey("first") ) {
			kvpairs.keySet().forEach(key -> {
				Object value = kvpairs.get(key);
				if (key == "id" && Long.parseLong(value.toString()) > 0 ) {
					customer.setId(Long.parseLong(value.toString()));
				} else if (key == "name") {
					if (customer.getFirstName() != null) {
						String fName = customer.getFirstName();
						customer.setName(fName, value.toString());
					}
					customer.setName(null, value.toString());
				} else if (key == "first") {
					if (customer.getLastName() != null) {
						String lName = customer.getLastName();
						customer.setName(value.toString(), lName);
					}
					customer.setName(value.toString(), null);
				} else if (key == "contacts") {
					String[] arrOfStr = value.toString().split(";");
					Arrays.asList(arrOfStr).forEach(contact -> customer.addContact(contact));
				}
			});
			return Optional.of(customer);
		} else {
			return Optional.empty();
		}
	}

}