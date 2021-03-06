package org.abdul.petclinic.controller;

import org.abdul.petclinic.model.Owner;
import org.abdul.petclinic.service.OwnerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    private static final String FIRST_NAME = "Mohideen Abdul Katheer";
    private static final String LAST_NAME = "Mohamed Amsa";
    private static final String CREATE_OR_UPDATE_OWNER_FORM_VIEW = "owners/createOrUpdateOwnerForm";
    private static final String FIND_OWNERS_VIEW = "owners/findOwners";
    private static final String OWNER_DETAILS_VIEW = "owners/ownerDetails";
    private static final String LIST_OWNERS_VIEW = "owners/ownersList";
    private static final String NEW_OWNER_URI = "/owners/new";
    private static final String SELECTED_OWNERS_URI = "/owners/selected";
    private static final String ALL_OWNERS_URI = "/owners";
    private static final String FIND_OWNER_URI = "/owners/find";

    @Mock
    private OwnerService ownerService;
    private Set<Owner> owners;
    @InjectMocks
    private OwnerController ownerController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        owners = new HashSet<>();
        owners.add(Owner.builder().id(1L).build());
        owners.add(Owner.builder().id(2L).build());
        owners.add(Owner.builder().id(3L).build());

        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
    }

    @Test
    public void listOwners() throws Exception {
        //given
        when(ownerService.findAll()).thenReturn(owners);
        //then
        mockMvc.perform(get(ALL_OWNERS_URI))
                .andExpect(status().is(200))
                .andExpect(view().name("owners/index"))
                .andExpect(model().attribute("owners", Matchers.hasSize(3)));
        verify(ownerService, times(1)).findAll();
    }

    @Test
    public void showOwner() throws Exception {
        //given
        when(ownerService.findById(1L)).thenReturn(Owner.builder().id(1L).firstName(FIRST_NAME).build());

        //when
        mockMvc.perform(get("/owners/1"))
                .andExpect(status().is(200))
                .andExpect(view().name(OWNER_DETAILS_VIEW))
                .andExpect(model().attribute("owner", hasProperty("id", is(1L))))
                .andExpect(model().attribute("owner", hasProperty("firstName", is(FIRST_NAME))));
    }

    @Test
    public void initFindOwnerForm() throws Exception {
        //when
        mockMvc.perform(get(FIND_OWNER_URI))
                .andExpect(status().is(200))
                .andExpect(view().name(FIND_OWNERS_VIEW));
    }

    @Test
    public void shouldReturnNotFoundErrorWhenOwnersNotFound() throws Exception {
        //given
        Owner owner = Owner.builder().lastName(LAST_NAME).build();
        when(ownerService.findByLastNameLike(LAST_NAME)).thenReturn(Collections.emptyList());

        //when
        mockMvc.perform(get(SELECTED_OWNERS_URI).param("lastName", LAST_NAME))
                .andExpect(status().is(200))
                .andExpect(view().name(FIND_OWNERS_VIEW))
                .andExpect(model().hasErrors());
    }

    @Test
    public void shouldForwardToDisplaySingleOwnerDetailsWhenSingleOwnerFound() throws Exception {
        //given
        Owner owner = Owner.builder()
                .id(1L)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
        when(ownerService.findByLastNameLike("Mohamed Amsa")).thenReturn(Collections.singletonList(owner));

        //when
        mockMvc.perform(get(SELECTED_OWNERS_URI).param("lastName", LAST_NAME))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/1"));
    }

    @Test
    public void shouldReturnViewForListingOwnerDetailsWhenMultipleOwnerFound() throws Exception {
        //given
        Owner owner1 = Owner.builder()
                .id(1L)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
        Owner owner2 = Owner.builder()
                .id(2L)
                .firstName("Faizal Ahamed")
                .lastName(LAST_NAME)
                .build();
        when(ownerService.findByLastNameLike(LAST_NAME)).thenReturn(Arrays.asList(owner1, owner2));

        //when
        mockMvc.perform(get(SELECTED_OWNERS_URI).param("lastName", LAST_NAME))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("selections", hasSize(2)))
                .andExpect(view().name(LIST_OWNERS_VIEW));
    }

    @Test
    public void testInitCreateForm() throws Exception {
        //then
        mockMvc.perform(get(NEW_OWNER_URI))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(CREATE_OR_UPDATE_OWNER_FORM_VIEW))
                .andExpect(model().attributeExists("owner"));
    }

    @Test
    public void testProcessCreateForm() throws Exception {
        //given
        Owner owner = Owner.builder().id(1L).firstName(FIRST_NAME).lastName(LAST_NAME).build();
        when(ownerService.save(owner)).thenReturn(owner);

        //then
        mockMvc.perform(post(NEW_OWNER_URI).flashAttr("owner", owner))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/1"));
    }

    @Test
    public void shouldPassCommandObjectRetrievedFromServiceToFormIfOwnerExists() throws Exception {
        //given
        when(ownerService.findById(1L)).thenReturn(Owner.builder().id(1L).build());

        //then
        mockMvc.perform(get("/owners/1/edit"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(CREATE_OR_UPDATE_OWNER_FORM_VIEW))
                .andExpect(model().attribute("owner", hasProperty("id", is(1L))));
    }

    @Test
    public void shouldRedirectToCreateNewOwnerFormIfOwnerNotExists() throws Exception {
        //given
        when(ownerService.findById(1L)).thenReturn(null);

        //then
        mockMvc.perform(get("/owners/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/new"));
    }

    @Test
    public void testProcessUpdateForm() throws Exception {
        //given
        Owner owner = Owner.builder().id(1L).build();
        when(ownerService.save(owner)).thenReturn(owner);

        //then
        mockMvc.perform(post("/owners/1/edit").flashAttr("owner", owner))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/1"));
    }
}