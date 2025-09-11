package com.chaletHub.backend.controller;

import com.chaletHub.backend.model.Chalet;
import com.chaletHub.backend.repository.ChaletRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chalets")
@CrossOrigin(origins = "http://localhost:5173") // autorise ton frontend Vue
public class ChaletController {

  private final ChaletRepository repo;

  public ChaletController(ChaletRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Chalet> getAll() {
    return repo.findAll();
  }

  @PostMapping
  public Chalet addChalet(@RequestBody Chalet chalet) {
    return repo.save(chalet);
  }
}
