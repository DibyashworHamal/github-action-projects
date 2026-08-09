package np.edu.nast.ebs.controller.web;

import jakarta.validation.Valid;
import np.edu.nast.ebs.dto.request.OrganizerPaymentDTO;
import np.edu.nast.ebs.security.CustomUserDetails;
import np.edu.nast.ebs.service.OrganizerRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
@PreAuthorize("hasRole('ORGANIZER')") 
public class PaymentWebController {

    private final OrganizerRequestService requestService;

    @Autowired
    public PaymentWebController(OrganizerRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/make")
    public String showPaymentPage(@RequestParam Integer requestId, Model model) {
        // Prepare a DTO for the form. It will hold the data the user submits.
        OrganizerPaymentDTO paymentDto = new OrganizerPaymentDTO();
        paymentDto.setRequestId(requestId);

        model.addAttribute("paymentDto", paymentDto);
        return "organizer/make_payment";
    }

    @PostMapping("/make")
    public String processPayment(@Valid @ModelAttribute("paymentDto") OrganizerPaymentDTO paymentDto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {

        // If validation fails (e.g., amount < 100), return to the form to show errors.
        if (bindingResult.hasErrors()) {
            return "organizer/make_payment";
        }

        try {
            // Service method now accepts the amount
            requestService.processPaymentForRequest(
                paymentDto.getRequestId(),
                userDetails.getUser().getUserId(),
                paymentDto.getAmount()
            );

            redirectAttributes.addFlashAttribute("successMessage", "Payment successful! Your request will now be reviewed by an admin.");
            return "redirect:/organizer/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment failed: " + e.getMessage());
            return "redirect:/organizer/dashboard";
        }
    }

    @GetMapping("/success")
    public String showPaymentSuccess() {
        return "organizer/payment_success";
    }
}