package org.abdul.petclinic.map;

import org.abdul.petclinic.model.Pet;
import org.abdul.petclinic.service.CrudService;
import org.abdul.petclinic.service.PetService;

import java.util.Set;

public class PetServiceMap extends AbstractMapService<Pet, Long> implements PetService {
   @Override
   public Set<Pet> findAll() {
      return super.findAll();
   }

   @Override
   public void deleteById(Long id) {
      super.deleteById(id);
   }

   @Override
   public void delete(Pet pet) {
      super.delete(pet);
   }

   @Override
   public Pet save(Pet pet) {
      return super.save(pet.getId(), pet);
   }

   @Override
   public Pet findById(Long id) {
      return super.findById(id);
   }
}
