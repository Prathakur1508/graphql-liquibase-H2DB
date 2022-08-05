package com.stream.stringopti.controller;



import com.stream.stringopti.dao.StreamRepository;
import com.stream.stringopti.entity.StreamOpti;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class StreamController {
    @Autowired
    private StreamRepository streamRepository;

    private GraphQL graphQL;

    @Value("classpath:stream.graphql")
    private Resource schemaResource;

    @PostConstruct
    public void loadSchema() throws IOException
    {
        File schemaFile=schemaResource.getFile();
        TypeDefinitionRegistry typeDefinitionRegistry=new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring=buildWiring();
        GraphQLSchema graphQLSchema=new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry,runtimeWiring);
        graphQL= GraphQL.newGraphQL(graphQLSchema).build();
    }
    private RuntimeWiring buildWiring()
    {
        DataFetcher<List<StreamOpti>>StreamAllFetcher= data->
        {
            return streamRepository.findAll();
        };
        DataFetcher<Optional<StreamOpti>>StreamFetcher= data->
        {
            return streamRepository.findById(data.getArgument("id"));
        };
        DataFetcher<List<StreamOpti>>StreamFetcherByName= data->
        {
            return streamRepository.findByName(data.getArgument("name"));
        };
        return RuntimeWiring.newRuntimeWiring().type("Query",typeWriting->
            typeWriting.dataFetcher("getAllStream",StreamAllFetcher).
                    dataFetcher("findStream",StreamFetcher).dataFetcher("findStreamByName",StreamFetcherByName)).build();

    }
    @PostMapping("/addStream")
    public String addStreamOpti(@RequestBody StreamOpti streamOptis)
    {
        streamRepository.save(streamOptis);
        return "record inserted";
    }
    @GetMapping("/getStream")
    public List<StreamOpti> getStreamOpti()
    {
        List<StreamOpti>streamOptis= streamRepository.findAll();
        return streamOptis;
    }
    @PostMapping("/getAll")
    public ResponseEntity<Object> getAll(@RequestBody String query)
    {
        ExecutionResult result=graphQL.execute(query);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }
    @PostMapping("/getStreamById")
    public ResponseEntity<Object> getStreamById(@RequestBody String query)
    {
        ExecutionResult result=graphQL.execute(query);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

}
