package logicBuilding;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Demo {

	static ObjectMapper objectMapper = new ObjectMapper();
	
	public static void main(String[] args) throws Exception {

		Pojo pojo=new Pojo();
		// Serialization
		pojo.setUserName("Veer");
		pojo.setAge(30);
		
		String data=objectMapper.writeValueAsString(pojo);
		System.out.println(data);
		
		// De-Serialization
        Pojo pojoObject=objectMapper.readValue(data,Pojo.class);

        System.out.println(pojoObject.getUserName());
        System.out.println(pojoObject.getAge());

	}

}
