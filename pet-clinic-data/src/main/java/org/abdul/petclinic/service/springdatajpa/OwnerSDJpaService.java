package org.abdul.petclinic.service.springdatajpa;

import org.abdul.petclinic.model.Owner;
import org.abdul.petclinic.repository.OwnerRepository;
import org.abdul.petclinic.service.OwnerService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Profile("springdatajpa")
public class OwnerSDJpaService implements OwnerService {
    private final OwnerRepository ownerRepository;

    public OwnerSDJpaService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public List<Owner> findByLastName(String lastName) {
        return ownerRepository.findByLastName(lastName);
    }

    @Override
    public List<Owner> findByLastNameLike(String lastNamePattern) {
        return ownerRepository.findByLastNameIsLike("%" + lastNamePattern + "%");
    }

    @Override
    public Set<Owner> findAll() {
        return StreamSupport
                .stream(ownerRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    @Override
    public Owner findById(Long id) {
        return ownerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Owner save(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Override
    public void delete(Owner owner) {
        ownerRepository.delete(owner);
    }

    @Override
    public void deleteById(Long id) {
        ownerRepository.deleteById(id);
    }
}
