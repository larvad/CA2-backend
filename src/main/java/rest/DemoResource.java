package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.ComboDTO;
import dtos.EventDto;
import dtos.PokemonDTO;
import dtos.RandomFactDTO;
import entities.Event;
import entities.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import entities.UserDto;
import facades.UserFacade;
import utils.EMF_Creator;
import utils.FactFetcher;
import utils.PokemonFetcher;


/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
@DeclareRoles({"user", "admin", "gusmester"})
public class DemoResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("select u from User u", User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("events")
    public String allEvents() {
        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<Event> query = em.createQuery("select e from Event e", Event.class);
            List<Event> events = query.getResultList();
            List<EventDto> eventDtos = new ArrayList<>();
            for (Event event : events) {
                eventDtos.add(new EventDto(event));
            }

            return GSON.toJson(eventDtos);
        } finally {
            em.close();
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed({"user"})
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed({"admin"})
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("populate")
    @RolesAllowed({"admin"})
    public String populateDB() {
        FACADE.populate();
        return "{\"msg\":\"DB populated\"}";


//        try {
//            TypedQuery<User> query = em.createQuery ("select u from User u",entities.User.class);
//            List<User> users = query.getResultList();
//            return "[" + users.size() + "]";
//        } finally {
//            em.close();
//        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("pokemon")
//    @RolesAllowed({"user", "admin"})
    public String getPokeInfo(String pokemon) throws IOException, ExecutionException, InterruptedException {
        String query;
        PokemonDTO pokemonDTO;
        RandomFactDTO randomFactDTO;
        JsonObject json = JsonParser.parseString(pokemon).getAsJsonObject();
        query = json.get("query").getAsString();

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<PokemonDTO> futurePKMN = executor.submit(() -> PokemonFetcher.getData(query));
        Future<RandomFactDTO> futureRNDF = executor.submit(FactFetcher::getFact);
        pokemonDTO = futurePKMN.get();
        randomFactDTO = futureRNDF.get();
        return GSON.toJson(new ComboDTO(pokemonDTO, randomFactDTO));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("pokemondeck")
    public String getPokemonDeck(String deckSize) throws IOException, ExecutionException, InterruptedException {
        int size;
        PokemonDTO pokemonDTO;
        RandomFactDTO randomFactDTO;
        List<ComboDTO> comboDTOs = new ArrayList<>();

        JsonObject json = JsonParser.parseString(deckSize).getAsJsonObject();
        size = Integer.parseInt(json.get("deckSize").getAsString());

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<PokemonDTO>> futuresPKMN = new ArrayList<>();
        List<Future<RandomFactDTO>> futuresRNDF = new ArrayList<>();
        Future<PokemonDTO> futurePKMN;
        for (int i = 0; i <= size-1; i++) {
            String finalI = String.valueOf((int) (Math.random() * 904 + 1));
            futurePKMN = executor.submit(() -> PokemonFetcher.getData(finalI));
            futuresPKMN.add(futurePKMN);
            Future<RandomFactDTO> futureRNDF = executor.submit(FactFetcher::getFact);
            futuresRNDF.add(futureRNDF);
        }

        for (int i = 0; i <= size-1; i++) {
            pokemonDTO = futuresPKMN.get(i).get();
            randomFactDTO = futuresRNDF.get(i).get();
            comboDTOs.add(new ComboDTO(pokemonDTO, randomFactDTO));
        }
        return GSON.toJson(comboDTOs);
    }

    @POST
    @Path("signup")
    @Consumes("application/json")
    @Produces("application/json")
    public String createUser(String userJSON) { // input is the body of the request, generated in the frontend
        JsonObject json = JsonParser.parseString(userJSON).getAsJsonObject();
        String username = json.get("userName").getAsString();
        String password = json.get("userPass").getAsString();
        User user = new User(username, password);
//            if (!Objects.equals(newFullPersonDTO.getEmail(), null)
//                    && !Objects.equals(newFullPersonDTO.getFirstName(), null)
//                    && !Objects.equals(newFullPersonDTO.getLastName(), null)) {
//                Person newPerson = new Person(newFullPersonDTO);
        User createdUser = FACADE.createUser(user);

        return GSON.toJson(createdUser);
//            } else {
//                List<String> msg = new ArrayList<>();
//                if (Objects.equals(newFullPersonDTO.getFirstName(), null)) msg.add("Field \"First name\" is required. ");
//                if (Objects.equals(newFullPersonDTO.getLastName(), null)) msg.add("Field \"Last name\" is required. ");
//                if (Objects.equals(newFullPersonDTO.getEmail(), null)) msg.add("Field \"Email\" is required. ");
//                throw new WebApplicationException(String.join("\n", msg), 400);
//            }

    }



    @POST
    @Path("booking")
    @Consumes("application/json")
    @Produces("application/json")
    public Integer addUserToEvent(String inputJSON) {

        JsonObject json = JsonParser.parseString(inputJSON).getAsJsonObject();
        String username = json.get("username").getAsString();
        String eventId = json.get("eventId").getAsString();
        String seats = json.get("seats").getAsString();

        Integer trueOrFalse = FACADE.userToEvent(eventId, username,seats);

        return trueOrFalse;


    }

    @GET
    @Path("myBookings")
    public String allMyBookings(String inputJSON) {

        JsonObject json = JsonParser.parseString(inputJSON).getAsJsonObject();
        String username = json.get("username").getAsString();

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<UserDto> query = em.createQuery("select e from User e", UserDto.class);
            List<UserDto> userDto = query.getResultList();
            return GSON.toJson(userDto);
        } finally {
            em.close();
        }

    }
}