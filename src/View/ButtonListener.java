package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that allows panel changing between searching for reviewers, searching for products, and getting a review rating based on action (button pressed)
 * Class that allows submit button to execute either a search for reviewers, search for products, or review rating for the user.
 * 
 * @author Jacoby Joukema
 * @version 1.0
 * @see UserInterface
*/

public class ButtonListener implements ActionListener
{

	@Override
	public void actionPerformed(ActionEvent e) 
	{
      /*
      * Using ActionListener and ActioanEvent libraries, ButtonListener can check if a String pressed equals a certain String value,
      * and if it does, then the UserInterface's submit() or changePane() methods are called to either call a submit action or change the panel
      *
      */
      
		String pressed = e.getActionCommand();
		System.out.println(pressed + " Pressed");
	
      		/*
            * If the button pressed has "Submit" as a String value, then UserInteface.submit() is called (which then checks what the current panel
            * is and processes user input to search for a product or reviewer results or give the user a rating.
            */
     
			if (pressed.equals("Submit"))
			{
				UserInterface.submit();
			}
      
      		/*
            * If the button pressed has "Reviewer" as a String value, switch to the panel to search for reviewers
            */
      
			if (pressed.equals("Reviewer"))
			{
				UserInterface.changePane(1);
			}
      
      		/*
            * If the button pressed has "Product" as a String value, switch to the panel to search for products
            */
      
			if (pressed.equals("Product"))
			{
				UserInterface.changePane(0);
			}
      
      		/*
            * If the button pressed has "Review" as a String value, switch to the panel to return the user a review accuracy rating
            */
			if (pressed.equalsIgnoreCase("Review"))
			{
				UserInterface.changePane(2);
			}
		} 
}