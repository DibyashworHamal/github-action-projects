package np.edu.nast.ebs.controller.web;

import np.edu.nast.ebs.dto.request.EventFormDTO;
import np.edu.nast.ebs.dto.response.BookingResponseDTO;
import np.edu.nast.ebs.dto.response.EventResponseDTO;
import np.edu.nast.ebs.dto.response.OrganizerRequestResponseDTO;
import np.edu.nast.ebs.model.Category;
import np.edu.nast.ebs.repository.CategoryRepository;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.service.BookingService;
import np.edu.nast.ebs.service.EventService;
import np.edu.nast.ebs.service.OrganizerRequestService;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid; 

@Controller
@RequestMapping("/organizer")
@PreAuthorize("hasRole('ORGANIZER')") 
public class OrganizerDashboardController {

    private final OrganizerRequestService organizerRequestService;
    private final EventService eventService;
    private final CategoryRepository categoryRepository; 
    private final BookingService bookingService;

    public OrganizerDashboardController(OrganizerRequestService organizerRequestService,
    		                             EventService eventService,
    		                             CategoryRepository categoryRepository,
    		                             BookingService bookingService) {
        this.organizerRequestService = organizerRequestService;
        this.eventService = eventService;
        this.categoryRepository = categoryRepository;
        this.bookingService = bookingService;
    }

    @ModelAttribute("approvalStatus")
    public String addApprovalStatusToModel(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "NOT_LOGGED_IN";
        }
        return organizerRequestService.findLatestRequestByUserId(userDetails.getUser().getUserId())
                .map(OrganizerRequestResponseDTO::getApprovalStatus)
                .orElse("NOT_REQUESTED"); 
    }

    @GetMapping("/dashboard")
    public String showOrganizerDashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("organizerName", userDetails.getUser().getFullName());
        return "organizer/organizer_dashboard";
    }

	 @GetMapping("/events/add")
	 public String showAddEventForm(Model model) {
	    if (!model.containsAttribute("eventForm")) {
	        model.addAttribute("eventForm", new EventFormDTO());
	    }
	    List<Category> categories = categoryRepository.findAll();
	    model.addAttribute("categories", categories);
	    return "organizer/add-event";
	}

    @PostMapping("/events/add")
    public String processAddEventForm(@Valid @ModelAttribute("eventForm") EventFormDTO eventForm,
                                      BindingResult result,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (result.hasErrors()) {
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            return "organizer/add-event";
        }
        try {
            eventService.createEventFromForm(eventForm, userDetails.getUser());
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");
            // Redirect to the view events page to see the new event immediately
            return "redirect:/organizer/events/view"; 
        } catch (Exception e) {
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("errorMessage", "Error creating event: " + e.getMessage());
            return "organizer/add-event";
        }
    }
	    
    @GetMapping("/events/view")
    public String showMyEvents(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<EventResponseDTO> myEvents = eventService.getEventsByOrganizer(userDetails.getUser());
        model.addAttribute("events", myEvents);
        return "organizer/view-events";
    }

    @GetMapping("/events/edit/{id}")
    public String showEditEventForm(@PathVariable("id") Integer eventId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        EventResponseDTO eventDTO = eventService.getById(eventId);
 
        if (!eventDTO.getOrganizerId().equals(userDetails.getId())) {
            return "redirect:/access-denied";
        }
        EventFormDTO formDTO = new EventFormDTO();
        formDTO.setEventName(eventDTO.getTitle());
        formDTO.setEventDescription(eventDTO.getDescription());
        formDTO.setStartDateTime(eventDTO.getStartDateTime());
        formDTO.setEndDateTime(eventDTO.getEndDateTime());
        formDTO.setLocation(eventDTO.getLocation());
        formDTO.setTotalTickets(eventDTO.getTotalTickets());
        formDTO.setTicketPrice(eventDTO.getPrice());
        formDTO.setOrganizerName(eventDTO.getOrganizerName());
        formDTO.setOrganizerContact(eventDTO.getOrganizerContact());
        formDTO.setEventWebsite(eventDTO.getEventWebsite());
        formDTO.setFeatured(eventDTO.isFeatured());
        formDTO.setRegistrationRequired(eventDTO.isRegistrationRequired());
        

        if(eventDTO.getCustomCategory() != null) {
            formDTO.setCategoryId(-1); 
            formDTO.setOtherCategoryName(eventDTO.getCustomCategory());
        } else {
            formDTO.setCategoryId(eventDTO.getCategoryId());
        }

        model.addAttribute("eventForm", formDTO);
        model.addAttribute("currentEvent", eventDTO); 
        model.addAttribute("eventId", eventId);
        model.addAttribute("categories", categoryRepository.findAll());
        
        return "organizer/edit-event"; 
    }
    
    @PostMapping("/events/edit/{id}")
    public String processUpdateEventForm(@PathVariable("id") Integer eventId,
                                         @Valid @ModelAttribute("eventForm") EventFormDTO eventForm,
                                         BindingResult result,
                                         @AuthenticationPrincipal CustomUserDetails userDetails,
                                         RedirectAttributes redirectAttributes, 
                                         Model model) {
        
        // Security check
        EventResponseDTO existingEvent = eventService.getById(eventId);
        if (!existingEvent.getOrganizerId().equals(userDetails.getId())) {
            return "redirect:/access-denied";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("eventId", eventId);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("currentEvent", existingEvent);
            return "organizer/edit-event";
        }

        try {
            eventService.updateEventFromForm(eventId, eventForm);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
            return "redirect:/organizer/events/view";
        } catch (Exception e) {
            model.addAttribute("eventId", eventId);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("currentEvent", existingEvent);
            model.addAttribute("errorMessage", "Error updating event: " + e.getMessage());
            return "organizer/edit-event";
        }
    }
    
    @GetMapping("/bookings")
    public String showMyBookings(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsForOrganizer(userDetails.getId());
        model.addAttribute("bookings", bookings);
        return "organizer/view-bookings"; 
    }

}