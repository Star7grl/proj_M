package ru.flamexander.spring.security;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.flamexander.spring.security.jwt.SecurityJwtApplication;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.dtos.RoomDto;
import ru.flamexander.spring.security.jwt.dtos.ServiceDto;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.entities.Room;
import ru.flamexander.spring.security.jwt.entities.Services;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.*;
import ru.flamexander.spring.security.jwt.service.RoleService;
import ru.flamexander.spring.security.jwt.service.RoomService;
import ru.flamexander.spring.security.jwt.service.ServiceService;
import ru.flamexander.spring.security.jwt.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.flamexander.spring.security.jwt.SecurityJwtApplication;
import ru.flamexander.spring.security.jwt.dtos.JwtRequest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SecurityJwtApplication.class)
class UnitTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RoleRepository roleRepository;

	@InjectMocks
	private UserService userService;

	@Mock
	private RoomRepository roomRepository;

	@Mock
	private RoleService roleService;

	@InjectMocks
	private RoomService roomService;

	@Mock
	private ServiceRepository serviceRepository;

	@Mock
	private ModelMapper modelMapper;


	@Mock
	private BookingRepository bookingRepository; // Mock BookingRepository

	@InjectMocks
	private ServiceService serviceService;

	@Test
	public void testFindByUsername() {
		String username = "testUser";
		User user = new User();
		user.setUsername(username);
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

		Optional<User> foundUser = userService.findByUsername(username);
		assertTrue(foundUser.isPresent());
		assertEquals(username, foundUser.get().getUsername());
	}

	@Test
	public void testCreateNewUser() {
		RegistrationUserDto dto = new RegistrationUserDto();
		dto.setUsername("newUser");
		dto.setPassword("password");
		dto.setEmail("email@example.com");

		Role userRole = new Role();
		userRole.setName("ROLE_USER");

		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(userRepository.save(Mockito.any(User.class))).thenReturn(new User());
		when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

		User createdUser = userService.createNewUser(dto);
		assertNotNull(createdUser);
	}





	@Test
	public void testGetRoomById() {
		Long roomId = 1L;
		Room room = new Room();
		room.setRoomId(roomId);
		when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

		Optional<Room> foundRoom = roomService.getRoomById(roomId);
		assertTrue(foundRoom.isPresent());
		assertEquals(roomId, foundRoom.get().getRoomId());
	}

	@Test
	public void testCreateRoom() {
		RoomDto roomDto = new RoomDto();
		roomDto.setRoomTitle("Test Room");
		roomDto.setPrice(100.0);
		roomDto.setImageUrls(Collections.singletonList("http://example.com/image.jpg"));  // Инициализация списка изображений

		Room room = new Room();
		room.setRoomId(1L);

		when(roomRepository.save(Mockito.any(Room.class))).thenReturn(room);
		when(modelMapper.map(Mockito.any(), Mockito.eq(Room.class))).thenReturn(room);  // Мокирование modelMapper

		Room createdRoom = roomService.createRoom(roomDto);
		assertNotNull(createdRoom);
		assertEquals(1L, createdRoom.getRoomId());
	}

	@Test
	public void testSaveService() {
		ServiceDto serviceDto = new ServiceDto();
		serviceDto.setServiceName("Test Service");
		serviceDto.setServicePrice(50.0);

		Services service = new Services();
		service.setServiceId(1L);

		when(modelMapper.map(Mockito.any(), Mockito.eq(Services.class))).thenReturn(service); // Мокируем маппинг
		when(serviceRepository.save(Mockito.any(Services.class))).thenReturn(service);
		when(modelMapper.map(Mockito.any(), Mockito.eq(ServiceDto.class))).thenReturn(serviceDto);

		ServiceDto savedService = serviceService.save(serviceDto);
		assertNotNull(savedService);
	}
	@Test
	public void testSearchRoomsByTitle() {
		String roomTitle = "Test Room";
		Room room = new Room();
		room.setRoomId(1L);
		room.setRoomTitle(roomTitle);

		when(roomRepository.findByRoomTitleContainingIgnoreCase(roomTitle)).thenReturn(Collections.singletonList(room));

		List<Room> foundRooms = roomService.searchByTitle(roomTitle);
		assertNotNull(foundRooms);
		assertEquals(1, foundRooms.size());
		assertEquals(roomTitle, foundRooms.get(0).getRoomTitle());
	}
	@Test
	public void testDeleteRoom() {
		Long roomId = 1L;
		Room room = new Room();
		room.setRoomId(roomId);

		when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
		doNothing().when(roomRepository).deleteById(roomId);
		when(bookingRepository.findAllByRoomId(roomId)).thenReturn(Collections.emptyList()); // Mock bookingRepository.findAllByRoomId

		roomService.deleteRoom(roomId);

		verify(roomRepository, times(1)).deleteById(roomId);
	}

	@Test
	public void testUpdateRoom() {
		Long roomId = 1L;
		RoomDto roomDto = new RoomDto();
		roomDto.setRoomTitle("Updated Test Room");
		roomDto.setPrice(120.0);
		roomDto.setImageUrls(Collections.singletonList("http://example.com/image.jpg")); // Initialize imageUrls

		Room existingRoom = new Room();
		existingRoom.setRoomId(roomId);
		existingRoom.setRoomTitle("Test Room");
		existingRoom.setPrice(100.0);

		when(roomRepository.findById(roomId)).thenReturn(Optional.of(existingRoom));
		when(roomRepository.save(Mockito.any(Room.class))).thenReturn(existingRoom);
		when(modelMapper.map(Mockito.any(), Mockito.eq(Room.class))).thenReturn(existingRoom);
		Room updatedRoom = roomService.updateRoom(roomId, roomDto);
		assertNotNull(updatedRoom);
		assertEquals("Updated Test Room", updatedRoom.getRoomTitle());
		assertEquals(120.0, updatedRoom.getPrice());
	}

}



@SpringBootTest(classes = SecurityJwtApplication.class)
@AutoConfigureMockMvc
class IntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testLogin() throws Exception {
		JwtRequest loginRequest = new JwtRequest();
		loginRequest.setUsername("admin");
		loginRequest.setPassword("Admin123!");  //Укажите правильный пароль

		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"admin\",\"password\":\"Admin123!\"}"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "ADMIN") //Добавим пользователя с ролью ADMIN
	public void testGetAllBookings() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bookings")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}


	@Test
	public void testGetAllRooms() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/rooms")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "ADMIN")//Добавим пользователя с ролью ADMIN
	public void testGetAllUsers() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}



}
