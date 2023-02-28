package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.JCheckBox;
//import javax.swing.JComboBox;

import orsc.ORSCharacter;

/**
 * Asgarnian Hobs Peninsula - By Kaila
 * Start in Fally East bank with Armor or Hobs Peninsula
 * Food IN BANK REQUIRED (reg atk str optional)
 *
 * Author - Kaila
 */
public class K_HobsPeninsula extends IdleScript {

	JFrame scriptFrame = null;
	boolean guiSetup = false;
	boolean scriptStarted = false;
	boolean potUp = true;

	int totalGuam = 0;
	int totalMar = 0;
	int totalTar = 0;
	int totalHar = 0;
	int totalRan = 0;
	int totalIrit = 0;
	int totalAva = 0;
	int totalKwuarm = 0;
	int totalCada = 0;
	int totalDwarf = 0;
    int totalLaw = 0;
    int totalNat = 0;
	int totalDeath = 0;
	int totalBlood = 0;
    int totalLoop = 0;
    int totalTooth = 0;
    int totalLeft = 0;
    int totalSpear = 0;
	int totalGems = 0;
	int totalLimp = 0;
    int totalTrips = 0;
	int foodWithdrawAmount = 1;

	int[] bones = {20, 413, 604, 814};
	int[] attackPot = {476,475,474};  //reg attack pot
	int[] strPot = {224,223,222}; //reg str pot
	int foodId = -1;
	int[] foodIds = { 546, 370, 367, 373 }; //cooked shark, swordfish, tuna, lobster

	int[] loot = {
			526, 	 //tooth half
			527, 	 //loop half
			1277, 	 //shield (left) half
			1092, 	 //rune spear
			160, 	 //saph
			159, 	 //emerald
			158, 	 //ruby
			157,	 //diamond

			33,		 //air rune
			34, 	 //Earth rune
			31,		 //fire rune
			32,		 //water rune
			36,		 //body runes
			46,		 //cosmic
			40,	 	 // nature rune
			42, 	 // law rune
			35, 	 //mind rune
			41,		 //chaos rune
			38, 	 //death rune
			619, 	 //blood rune

			220, 	 //limps
			165,     //Grimy Guam
			435,     //Grimy mar
			436,     //Grimy tar
			437,     //Grimy har
			438, 	 //Grimy ranarr
			439,  	 //Grimy irit
			440,  	 //Grimy ava
			441,	 //Grimy kwu
			442, 	 //Grimy cada
			443, 	 //Grimy dwu

			11, 	 //bronze arrow
			1026,    //unholy mould
			10//, 	 //coins
			//413,	 //Big bones
			//20       //bones

			};

	long startTime;
	long startTimestamp = System.currentTimeMillis() / 1000L;

	public boolean isWithinLootzone(int x, int y) {
		return controller.distance(363, 610, x, y) <= 15; //center of lootzone
	}
	
	public int start(String parameters[]) {

		if (scriptStarted) {
			controller.displayMessage("@red@Asgarnian Hobs Peninsula - By Kaila");
			controller.displayMessage("@red@Start in Fally East bank with Armor or Hobs Peninsula");
			controller.displayMessage("@red@Food in Bank REQUIRED");
			if(controller.isInBank()) {
				controller.closeBank();
			}
			if(controller.currentY() < 595) {
				bank();
				BankToPeninsula();
				controller.sleep(1380);
			}
			scriptStart();
		} else {
			if (parameters[0].equals("")) {
				if (!guiSetup) {
					setupGUI();
					guiSetup = true;
				}
			} else {
				try {
					foodWithdrawAmount = Integer.parseInt(parameters[0]);

				} catch (Exception e) {
					System.out.println("Could not parse parameters!");
					controller.displayMessage("@red@Could not parse parameters!");
					controller.stop();
				}
			}
		}
		return 1000; //start() must return a int value now. 
	}

	public void scriptStart() {
		while (controller.isRunning()) {

			eat();
			buryBones();

			if (controller.getInventoryItemCount() < 30) {

				boolean lootPickedUp = false;
				for (int lootId : loot) {
					int[] coords = controller.getNearestItemById(lootId);
					if (coords != null && this.isWithinLootzone(coords[0], coords[1])) {
						controller.setStatus("@yel@Looting..");
						controller.walkTo(coords[0], coords[1]);
						controller.pickupItem(coords[0], coords[1], lootId, true, true);
						controller.sleep(618);
					}
				}
				if (lootPickedUp) //we don't want to start to pickup loot then immediately attack a npc
					continue;

				if (potUp == true) {
					if (controller.getCurrentStat(controller.getStatId("Attack")) == controller.getBaseStat(controller.getStatId("Attack"))) {
						if (controller.getInventoryItemCount(attackPot[0]) > 0 || controller.getInventoryItemCount(attackPot[1]) > 0 || controller.getInventoryItemCount(attackPot[2]) > 0) {
							attackBoost();
						}
					}
					if (controller.getCurrentStat(controller.getStatId("Strength")) == controller.getBaseStat(controller.getStatId("Strength"))) {
						if (controller.getInventoryItemCount(strPot[0]) > 0 || controller.getInventoryItemCount(strPot[1]) > 0 || controller.getInventoryItemCount(strPot[2]) > 0) {
							strengthBoost();
						}
					}
				}
				if (!controller.isInCombat()) {
					int[] npcIds = { 67 };
					ORSCharacter npc = controller.getNearestNpcByIds(npcIds, false);
					if (npc != null) {
						controller.setStatus("@yel@Attacking..");
						//controller.walktoNPC(npc.serverIndex,1);
						controller.attackNpc(npc.serverIndex);
						controller.sleep(1000);
					} else {
						controller.sleep(1000);
						if (controller.currentX() != 364 || controller.currentY() != 607) {
							controller.walkTo(364, 607);
							controller.sleep(1000);
						}
					}
				}
				controller.sleep(320);
			}
			if (controller.getInventoryItemCount() > 29 || controller.getInventoryItemCount(foodId) == 0) {
				controller.setStatus("@yel@Banking..");
				PeninsulaToBank();
				bank();
				BankToPeninsula();
				controller.sleep(618);
			}
		}
	}

		

	


	
	
	public void bank() {

		controller.setStatus("@yel@Banking..");
		controller.openBank();
		controller.sleep(640);

		if (controller.isInBank()) {
			
			totalGuam = totalGuam + controller.getInventoryItemCount(165);
			totalMar = totalMar + controller.getInventoryItemCount(435);
			totalTar = totalTar + controller.getInventoryItemCount(436);
			totalHar = totalHar + controller.getInventoryItemCount(437);
			totalRan = totalRan + controller.getInventoryItemCount(438);
			totalIrit = totalIrit + controller.getInventoryItemCount(439);
			totalAva = totalAva + controller.getInventoryItemCount(440);
			totalKwuarm = totalKwuarm + controller.getInventoryItemCount(441);
			totalCada = totalCada + controller.getInventoryItemCount(442);
			totalDwarf = totalDwarf + controller.getInventoryItemCount(443);
			totalLaw = totalLaw + controller.getInventoryItemCount(42);
			totalNat = totalNat + controller.getInventoryItemCount(40);
			totalLimp = totalLimp + controller.getInventoryItemCount(220);
			totalLoop = totalLoop + controller.getInventoryItemCount(527);
			totalTooth = totalTooth + controller.getInventoryItemCount(526);
			totalLeft = totalLeft + controller.getInventoryItemCount(1277);
			totalGems = totalGems
					+ controller.getInventoryItemCount(160)
					+ controller.getInventoryItemCount(159)
					+ controller.getInventoryItemCount(158)
					+ controller.getInventoryItemCount(157);
			totalSpear = totalSpear + controller.getInventoryItemCount(1092);

			if (controller.getInventoryItemCount() > 2) {
				for (int itemId : controller.getInventoryItemIds()) {
					if (itemId != 476 && itemId != 475 && itemId != 224 && itemId != 223) {  //dont deposit partial potions!
						controller.depositItem(itemId, controller.getInventoryItemCount(itemId));
					}
				}
			}
			controller.sleep(640);
			if (potUp == true) {
				if (controller.getInventoryItemCount(attackPot[0]) < 1 && controller.getInventoryItemCount(attackPot[1]) < 1 && controller.getInventoryItemCount(attackPot[2]) < 1) {  //withdraw 10 shark if needed
					controller.withdrawItem(attackPot[2], 1);
					controller.sleep(340);
				}
				if (controller.getInventoryItemCount(strPot[0]) < 1 && controller.getInventoryItemCount(strPot[1]) < 1 && controller.getInventoryItemCount(strPot[2]) < 1) {  //withdraw 10 shark if needed
					controller.withdrawItem(strPot[2], 1);
					controller.sleep(340);
				}
			}
			if(controller.getInventoryItemCount(foodId) < foodWithdrawAmount) {  //withdraw foods
				controller.withdrawItem(foodId, foodWithdrawAmount);
				controller.sleep(340);
			}
			if(controller.getBankItemCount(foodId) == 0) {
				controller.setStatus("@red@NO Food in the bank, Logging Out!.");
				controller.setAutoLogin(false);
				controller.logout();
				if(!controller.isLoggedIn()) {
					controller.stop();
					return;
				}
			}
			controller.closeBank();
			controller.sleep(640);
		}
	}

	public void buryBones() {
		if(!controller.isInCombat()) {
			for(int id : bones) {
				if(controller.getInventoryItemCount(id) > 0) {
					controller.setStatus("@yel@Burying..");
					controller.itemCommand(id);

					controller.sleep(618);
					buryBones();
				}
			}
		}
	}

	public void eat() {
		int eatLvl = controller.getBaseStat(controller.getStatId("Hits")) - 20;
		
		
		if(controller.getCurrentStat(controller.getStatId("Hits")) < eatLvl) {
			
			while(controller.isInCombat()) {
				controller.setStatus("@red@Leaving combat..");
				controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
				controller.sleep(250);
			}
			controller.setStatus("@red@Eating..");
			
			boolean ate = false;
			
			for(int id : controller.getFoodIds()) {
				if(controller.getInventoryItemCount(id) > 0) {
					controller.itemCommand(id);
					controller.sleep(700);
					ate = true;
					break;
				}
			}
			if(!ate) { //only activates if hp goes to -20 again THAT trip, will bank and get new shark usually
				controller.setStatus("@yel@Banking..");
				PeninsulaToBank();
				bank();
				BankToPeninsula();
				controller.sleep(618);
				}
			}
		}
	public void attackBoost() {
		while(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
			controller.sleep(250);
		}
		if(controller.getInventoryItemCount(attackPot[0]) > 0) {
			controller.itemCommand(attackPot[0]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(attackPot[1]) > 0) {
			controller.itemCommand(attackPot[1]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(attackPot[2]) > 0) {
			controller.itemCommand(attackPot[2]);
			controller.sleep(320);
			return;
		}
		return;
	}

	public void strengthBoost() {
		while(controller.isInCombat()) {
			controller.setStatus("@red@Leaving combat..");
			controller.walkTo(controller.currentX(), controller.currentY(), 0, true);
			controller.sleep(250);
		}
		if(controller.getInventoryItemCount(strPot[0]) > 0) {
			controller.itemCommand(strPot[0]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(strPot[1]) > 0) {
			controller.itemCommand(strPot[1]);
			controller.sleep(320);
			return;
		}
		if(controller.getInventoryItemCount(strPot[2]) > 0) {
			controller.itemCommand(strPot[2]);
			controller.sleep(320);
			return;
		}
		return;
	}
	public void PeninsulaToBank() {
    	controller.setStatus("@gre@Walking to Bank..");
		controller.walkTo(361,614);
		controller.walkTo(356, 619);
		controller.walkTo(346, 619);
		controller.walkTo(336, 619);
		controller.walkTo(326, 619);
		controller.walkTo(319, 619);
		controller.walkTo(314, 614);
		controller.walkTo(309, 609);
		controller.walkTo(309, 607);
		controller.walkTo(299, 597);
		controller.walkTo(291, 589);
		controller.walkTo(291, 576);
		controller.walkTo(286, 571);
		controller.sleep(640);
		totalTrips = totalTrips + 1;
    	controller.setStatus("@gre@Done Walking..");
	}
	
    public void BankToPeninsula() {
    	controller.setStatus("@gre@Walking to Penensula..");
		controller.walkTo(287, 572);
		controller.walkTo(291, 576);
		controller.walkTo(291, 589);
		controller.walkTo(299, 597);
		controller.walkTo(309, 607);
		controller.walkTo(309, 609);
		controller.walkTo(314, 614);
		controller.walkTo(319, 619);
		controller.walkTo(326, 619);
		controller.walkTo(336, 619);
		controller.walkTo(346, 619);
		controller.walkTo(356, 619);
		controller.walkTo(361,614);
    	controller.setStatus("@gre@Done Walking..");
	}
	
	
    
	//GUI stuff below (icky)

	public void setValuesFromGUI(JCheckBox potUpCheckbox) {
		if (potUpCheckbox.isSelected()) {
			potUp = true;
		} else {
			potUp = false;
		}
	}
	
	public static void centerWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	public void setupGUI() {
		JLabel header = new JLabel("Asgarnian Hobs Peninsula - By Kaila");
		JLabel label1 = new JLabel("Start in Fally East bank with Armor");
		JLabel label2 = new JLabel("	or at Hobgoblin Peninsula");
		JLabel label3 = new JLabel("Food in bank REQUIRED (pots optional)");
		JCheckBox potUpCheckbox = new JCheckBox("Use regular Atk/Str Pots?", true);
		JLabel foodWithdrawAmountLabel = new JLabel("Food Withdraw amount:");
		JTextField foodWithdrawAmountField = new JTextField(String.valueOf(1));
		JLabel foodLabel = new JLabel("Type of Food:");
		JComboBox<String> foodField = new JComboBox<String>( new String[] { "Sharks", "Swordfish", "Tuna", "Lobsters" });
		JLabel blankLabel = new JLabel("          ");
		JButton startScriptButton = new JButton("Start");

		startScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!foodWithdrawAmountField.getText().equals(""))
					foodWithdrawAmount = Integer.parseInt(foodWithdrawAmountField.getText());
				setValuesFromGUI(potUpCheckbox);
				foodId = foodIds[foodField.getSelectedIndex()];
				scriptFrame.setVisible(false);
				scriptFrame.dispose();
				startTime = System.currentTimeMillis();
				scriptStarted = true;
			}
		});
		
		scriptFrame = new JFrame("Script Options");

		scriptFrame.setLayout(new GridLayout(0, 1));
		scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scriptFrame.add(header);
		scriptFrame.add(label1);
		scriptFrame.add(label2);
		scriptFrame.add(label3);
		scriptFrame.add(potUpCheckbox);
		scriptFrame.add(foodWithdrawAmountLabel);
		scriptFrame.add(foodWithdrawAmountField);
		scriptFrame.add(foodLabel);
		scriptFrame.add(foodField);
		scriptFrame.add(blankLabel);
		scriptFrame.add(startScriptButton);
		centerWindow(scriptFrame);
		scriptFrame.setVisible(true);
		scriptFrame.pack();
		scriptFrame.requestFocus();

	}
	public static String msToString(long milliseconds) {
		long sec = milliseconds / 1000;
		long min = sec / 60;
		long hour = min / 60;
		sec %= 60;
		min %= 60;
		DecimalFormat twoDigits = new DecimalFormat("00");

		return new String(twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec));
	}
	@Override
	public void paintInterrupt() {
		if (controller != null) {
			
			String runTime = msToString(System.currentTimeMillis() - startTime);
	    	int guamSuccessPerHr = 0;
    		int marSuccessPerHr = 0;
    		int tarSuccessPerHr = 0;
    		int harSuccessPerHr = 0;
    		int ranSuccessPerHr = 0;
    		int iritSuccessPerHr = 0;
    		int avaSuccessPerHr = 0;
    		int kwuSuccessPerHr = 0;
    		int cadaSuccessPerHr = 0;
    		int dwarSuccessPerHr = 0;
			int limpSuccessPerHr = 0;
    		int lawSuccessPerHr = 0;
    		int natSuccessPerHr = 0;
			int GemsSuccessPerHr = 0;
    		int TripSuccessPerHr = 0;
    		
	    	try {
	    		float timeRan = (System.currentTimeMillis() / 1000L) - startTimestamp;
	    		float scale = (60 * 60) / timeRan;
	    		guamSuccessPerHr = (int)(totalGuam * scale);
	    		marSuccessPerHr = (int)(totalMar * scale);
	    		tarSuccessPerHr = (int)(totalTar * scale);
	    		harSuccessPerHr = (int)(totalHar * scale);
	    		ranSuccessPerHr = (int)(totalRan * scale);
	    		iritSuccessPerHr = (int)(totalIrit * scale);
	    		avaSuccessPerHr = (int)(totalAva * scale);
	    		kwuSuccessPerHr = (int)(totalKwuarm * scale);
	    		cadaSuccessPerHr = (int)(totalCada * scale);
	    		dwarSuccessPerHr = (int)(totalDwarf * scale);
				limpSuccessPerHr = (int)(totalLimp * scale);
	    		lawSuccessPerHr = (int)(totalLaw * scale);
	    		natSuccessPerHr = (int)(totalNat * scale);
				GemsSuccessPerHr = (int)(totalGems * scale);
	    		TripSuccessPerHr = (int)(totalTrips * scale);
	    		
	    	} catch(Exception e) {
	    		//divide by zero
	    	}
	    	
			controller.drawString("@red@Hobgoblin Peninsula @gre@by Kaila", 330, 48, 0xFFFFFF, 1);
			controller.drawString("@whi@Guams: @gre@" + String.valueOf(this.totalGuam) + "@yel@ (@whi@" + String.format("%,d", guamSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 62, 0xFFFFFF, 1);
			controller.drawString("@whi@Marrentills: @gre@" + String.valueOf(this.totalMar) + "@yel@ (@whi@" + String.format("%,d", marSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 76, 0xFFFFFF, 1);
			controller.drawString("@whi@Tarromins: @gre@" + String.valueOf(this.totalTar) + "@yel@ (@whi@" + String.format("%,d", tarSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 90, 0xFFFFFF, 1);
			controller.drawString("@whi@Harralanders: @gre@" + String.valueOf(this.totalHar) + "@yel@ (@whi@" + String.format("%,d", harSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 104, 0xFFFFFF, 1);
			controller.drawString("@whi@Ranarrs: @gre@" + String.valueOf(this.totalRan) + "@yel@ (@whi@" + String.format("%,d", ranSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 118, 0xFFFFFF, 1);
			controller.drawString("@whi@Irit Herbs: @gre@" + String.valueOf(this.totalIrit) + "@yel@ (@whi@" + String.format("%,d", iritSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 132, 0xFFFFFF, 1);
			controller.drawString("@whi@Avantoes: @gre@" + String.valueOf(this.totalAva) + "@yel@ (@whi@" + String.format("%,d", avaSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 146, 0xFFFFFF, 1);
			controller.drawString("@whi@Kwuarms: @gre@" + String.valueOf(this.totalKwuarm) + "@yel@ (@whi@" + String.format("%,d", kwuSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 160, 0xFFFFFF, 1);
			controller.drawString("@whi@Cadantines: @gre@" + String.valueOf(this.totalCada) + "@yel@ (@whi@" + String.format("%,d", cadaSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 174, 0xFFFFFF, 1);
			controller.drawString("@whi@Dwarfs: @gre@" + String.valueOf(this.totalDwarf) + "@yel@ (@whi@" + String.format("%,d", dwarSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 188, 0xFFFFFF, 1);
			controller.drawString("@whi@Limpwurts: @gre@" + String.valueOf(this.totalLimp) + "@yel@ (@whi@" + String.format("%,d", limpSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 202, 0xFFFFFF, 1);
			controller.drawString("@whi@Laws: @gre@" + String.valueOf(this.totalLaw) + "@yel@ (@whi@" + String.format("%,d", lawSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 216, 0xFFFFFF, 1);
			controller.drawString("@whi@Nats: @gre@" + String.valueOf(this.totalNat) + "@yel@ (@whi@" + String.format("%,d", natSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 230, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Gems: @gre@" + String.valueOf(this.totalGems) + "@yel@ (@whi@" + String.format("%,d", GemsSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 244, 0xFFFFFF, 1);
			controller.drawString("@whi@Tooth: @gre@" + String.valueOf(this.totalTooth) + "@yel@ / @whi@Loop: @gre@" + String.valueOf(this.totalLoop), 350, 258, 0xFFFFFF, 1);
			controller.drawString("@whi@R.Spear: @gre@" + String.valueOf(this.totalSpear) + "@yel@ / @whi@Shield Half: @gre@" + String.valueOf(this.totalLeft), 350, 272, 0xFFFFFF, 1);
			controller.drawString("@whi@Total Trips: @gre@" + String.valueOf(this.totalTrips) + "@yel@ (@whi@" + String.format("%,d", TripSuccessPerHr) + "@yel@/@whi@hr@yel@)", 350, 286, 0xFFFFFF, 1);
			controller.drawString("@whi@Runtime: " + runTime, 350, 300, 0xFFFFFF, 1);
		}
	}
}
