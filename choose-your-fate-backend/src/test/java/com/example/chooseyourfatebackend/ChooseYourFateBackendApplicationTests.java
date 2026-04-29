package com.example.chooseyourfatebackend;

import dk.ek.gruppe2.chooseyourfate.ChooseYourFateBackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = ChooseYourFateBackendApplication.class)
class ChooseYourFateBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
