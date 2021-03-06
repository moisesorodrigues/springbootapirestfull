package br.com.springboot.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.springboot.models.ProdutoModel;
import br.com.springboot.repositories.ProdutoRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class ProdutoController {
	
	@Autowired
	ProdutoRepository produtoRepository;
	
	@GetMapping("/produtos")
	public ResponseEntity<List<ProdutoModel>> getAllProdutos(){
		List<ProdutoModel> produtosList = produtoRepository.findAll();
		
		if(produtosList.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			for(ProdutoModel produto : produtosList) {
				long id = produto.getIdProduto();
				produto.add(linkTo(methodOn(ProdutoController.class).getOneProduto(id)).withSelfRel());
			}
			return new ResponseEntity<List<ProdutoModel>>(produtosList, HttpStatus.OK);
		}
	}
	
	@GetMapping("/produtos/{id}")
	public ResponseEntity<ProdutoModel> getOneProduto(@PathVariable(value = "id") long id){
		Optional<ProdutoModel> produto = produtoRepository.findById(id);
		
		if(!produto.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			produto.get().add(linkTo(methodOn(ProdutoController.class).getAllProdutos()).withRel("Lista de Produtos"));
			return new ResponseEntity<ProdutoModel>(produto.get(), HttpStatus.OK);
		}
	}
	
	@PostMapping("/produtos")
	public ResponseEntity<ProdutoModel> saveProduto(@RequestBody @Valid ProdutoModel produto){
		return new ResponseEntity<ProdutoModel>(produtoRepository.save(produto), HttpStatus.CREATED);
	}
	
	@DeleteMapping("/produtos/{id}")
	public ResponseEntity<?> deleteProduto(@PathVariable(value = "id") long id){
		Optional<ProdutoModel> produto = produtoRepository.findById(id);
		
		if(!produto.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			produtoRepository.delete(produto.get());
			return new ResponseEntity<>(HttpStatus.OK);
		}
	}
	
	@PutMapping("/produtos/{id}")
	public ResponseEntity<ProdutoModel> updateProduto(@PathVariable(value = "id") long id, @RequestBody @Valid ProdutoModel produto){
		Optional<ProdutoModel> produtoAuxiliar = produtoRepository.findById(id);
		
		if(!produtoAuxiliar.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			produto.setIdProduto(produtoAuxiliar.get().getIdProduto());
			return new ResponseEntity<ProdutoModel>(produtoRepository.save(produto), HttpStatus.OK);
		}
	}

}
