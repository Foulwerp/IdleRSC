package scripting.idlescript;

import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Mines Addy/Mith/Coal in Hobgoblin Mine and banks in Edge! (some pk/death protection).
 *
 * <p>
 *
 * <p>This bot supports the "autostart" parameter to automatiically start the bot without gui.
 *
 * <p>
 *
 * <p>Start in Edge bank with Armor and Pickaxe.
 *
 * <p>Sharks in bank REQUIRED.
 *
 * <p>
 *
 * <p>Teleport if Pkers Attack Option.
 *
 * <p>31 Magic, Laws, Airs, and Earths required for Escape Tele.
 *
 * <p>Unselected, bot WALKS to Edge when Attacked.
 *
 * <p>Selected, bot walks to 19 wildy and teleports.
 *
 * <p>
 *
 * <p>Return to Hobs Mine after Escaping Option.
 *
 * <p>Unselected, bot will log out after escaping Pkers.
 *
 * <p>Selected, bot will grab more food and return.
 *
 * <p>
 *
 * <p>This bot supports the \"autostart\" parameter.
 *
 * <p>Defaults to Teleport Off, Return On.
 *
 * <p>@Author - Kaila
 */
public final class K_HobsMiner extends K_kailaScript {
  private static String isMining = "none";
  private static boolean teleportOut = false;
  private static boolean returnEscape = true;
  private static final int[] currentOre = {0, 0};
  private static final int[] addyIDs = {
    108, 231, 109
  }; // 108,231,109 (addy) 106,107 (mith) 110,111 (coal)  98 empty
  private static final int[] mithIDs = {106, 107};
  private static final int[] coalIDs = {110, 111};
  private static final int[] loot = {
    // loot RDT hob drops
    526, // tooth half
    527, // loop half
    1277, // shield (left) half
    1092, // rune spear
    160, // saph
    159, // emerald
    158, // ruby
    157, // diamond

    // loot common armor if another bot dies
    1318, // ring of wealth
    402, // rune leg
    400, // rune chain
    399, // rune med
    403, // rune sq
    404, // rune kite
    112, // rune full helm
    1262, // rune pic
    315, // Emerald Amulet of protection
    317, // Diamond Amulet of power
    522, // dragonstone ammy

    // loot "some" hobs drops
    38, // death rune
    619, // blood rune
    42, // laws
    40, // nats
    440, // Grimy ava
    441, // Grimy kwu
    442, // Grimy cada
    443, // Grimy dwu
  };

  private boolean isWithinLootzone(int x, int y) {
    return c.distance(225, 251, x, y) <= 30; // center of hobs mine lootzone
  }

  private boolean adamantiteAvailable() {
    return c.getNearestObjectByIds(addyIDs) != null;
  }

  private boolean mithrilAvailable() {
    return c.getNearestObjectByIds(mithIDs) != null;
  }

  private boolean coalAvailable() {
    return c.getNearestObjectByIds(coalIDs) != null;
  }

  private boolean rockEmpty() {
    if (currentOre[0] != 0) {
      return c.getObjectAtCoord(currentOre[0], currentOre[1]) == 98;
    } else {
      return true;
    }
  }

  private void startSequence() {
    c.displayMessage("@red@Hobs Miner- By Kaila");
    c.displayMessage("@red@Start in Edge bank with Armor and pickaxe");
    c.displayMessage("@red@Sharks/Laws/Airs/Earths IN BANK REQUIRED");
    c.displayMessage("@red@31 Magic Required for escape tele");
    if (c.isInBank()) {
      c.closeBank();
    }
    if (c.currentY() > 340) {
      bank();
      eat();
      bankToHobs();
      eat();
      c.sleep(1380);
    }
    if (c.currentY() > 270 && c.currentY() < 341) {
      bankToHobs();
      eat();
      c.sleep(1380);
    }
    if (orsc.Config.C_BATCH_PROGRESS_BAR) c.toggleBatchBars();
  }

  public int start(String[] parameters) {
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("auto")) {
        c.displayMessage("Auto-starting, teleport false, return escape true", 0);
        System.out.println("Auto-starting, teleport false, return escape true");
        teleportOut = false;
        returnEscape = true;
          guiSetup = true;
        scriptStarted = true;
      }
    }
      if (!guiSetup) {
          setupGUI();
          guiSetup = true;
      }
    if (scriptStarted) {
        guiSetup = false;
        scriptStarted = false;
      startTime = System.currentTimeMillis();
      startSequence();
      scriptStart();
    }

    return 1000; // start() must return an int value now.
  }

  private void scriptStart() {
    while (c.isRunning()) {

      eat();
      leaveCombat();

      if (c.getInventoryItemCount(546) == 0) {
        c.setStatus("@red@We've ran out of Food! Teleporting Away!.");
        hobsToTwenty();
        c.sleep(100);
        c.castSpellOnSelf(c.getSpellIdFromName("Lumbridge Teleport"));
        c.sleep(308);
        c.walkTo(120, 644);
        c.atObject(119, 642);
        c.walkTo(217, 447);
        c.sleep(618);
        bank();
        bankToHobs();
      }
      if (c.getInventoryItemCount() == 30) {

        goToBank();
      }
      if (c.getInventoryItemCount() < 30) {

        eat();
        leaveCombat();
        lootScript();
        if (rockEmpty() || !c.isBatching()) {
          isMining = "none";
          currentOre[0] = 0;
          currentOre[1] = 0;
        }
        if (c.isBatching()) {
          if (Objects.equals(isMining, "mithril")) {
            if (adamantiteAvailable()) {
              mine("adamantite");
            }
          }
          if (Objects.equals(isMining, "coal")) {
            if (adamantiteAvailable()) {
              mine("adamantite");
            } else if (mithrilAvailable()) {
              mine("mithril");
            }
          }
          c.sleep(1280);
        }
        leaveCombat();
        c.setStatus("@yel@Mining..");

        if (!c.isBatching() && Objects.equals(isMining, "none") && rockEmpty()) {
          if (adamantiteAvailable()) {
            mine("adamantite");
          } else if (mithrilAvailable()) {
            mine("mithril");
          } else if (coalAvailable()) {
            mine("coal");
          }
          c.sleep(1280);
        }
      }
    }
  }

  private void lootScript() {
    for (int lootId : loot) {
      try {
        int[] coords = c.getNearestItemById(lootId);
        if (coords != null && isWithinLootzone(coords[0], coords[1])) {
          c.setStatus("@yel@Looting..");
          c.walkToAsync(coords[0], coords[1], 0);
          c.pickupItem(coords[0], coords[1], lootId, true, false);
          c.sleep(640);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void mine(String i) {
    if (Objects.equals(i, "adamantite")) {
      int[] oreCoords = c.getNearestObjectByIds(addyIDs);
      if (oreCoords != null) {
        isMining = "adamantite";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    } else if (Objects.equals(i, "mithril")) {
      int[] oreCoords = c.getNearestObjectByIds(mithIDs);
      if (oreCoords != null) {
        isMining = "mithril";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    } else if (Objects.equals(i, "coal")) {
      int[] oreCoords = c.getNearestObjectByIds(coalIDs);
      if (oreCoords != null) {
        isMining = "coal";
        c.atObject(oreCoords[0], oreCoords[1]);
        currentOre[0] = oreCoords[0];
        currentOre[1] = oreCoords[1];
      }
    }
    c.sleep(1920);
  }

  private void bank() {

    c.setStatus("@yel@Banking..");
      c.openBank();
      c.sleep(640);
      if (!c.isInBank()) {
          waitForBankOpen();
      } else {

      totalCoal = totalCoal + c.getInventoryItemCount(155);
      totalMith = totalMith + c.getInventoryItemCount(153);
      totalAddy = totalAddy + c.getInventoryItemCount(154);
      totalSap = totalSap + c.getInventoryItemCount(160);
      totalEme = totalEme + c.getInventoryItemCount(159);
      totalRub = totalRub + c.getInventoryItemCount(158);
      totalDia = totalDia + c.getInventoryItemCount(157);

      for (int itemId : c.getInventoryItemIds()) {
        if (itemId != 546
            && itemId != 156
            && itemId != 1263
            && itemId != 1262) { // won't bank sharks, rune/bronze pick, or sleeping bags
          c.depositItem(itemId, c.getInventoryItemCount(itemId));
        }
      }
      c.sleep(1280); // increased sleep here to prevent double banking

      coalInBank = c.getBankItemCount(155);
      mithInBank = c.getBankItemCount(153);
      addyInBank = c.getBankItemCount(154);

      if (teleportOut) {
        if (c.getInventoryItemCount(33) < 3) { // withdraw 3 air
          c.withdrawItem(33, 3);
          c.sleep(640);
        }
        if (c.getInventoryItemCount(34) < 1) { // withdraw 1 earth
          c.withdrawItem(34, 1);
          c.sleep(640);
        }
        if (c.getInventoryItemCount(42) < 1) { // withdraw 1 law
          c.withdrawItem(42, 1);
          c.sleep(640);
        }
      }
      if (c.getInventoryItemCount(546) > 1) {
        c.depositItem(546, c.getInventoryItemCount(546) - 1);
        c.sleep(640);
      }
      if (c.getInventoryItemCount(546) < 1) { // withdraw 1 shark
        c.withdrawItem(546, 1);
        c.sleep(640);
      }
      if (c.getBankItemCount(546) == 0) {
        c.setStatus("@red@NO Sharks in the bank, Logging Out!.");
        c.setAutoLogin(false);
        c.sleep(5000);
        c.logout();
        if (!c.isLoggedIn()) {
          c.stop();
        }
      }
      c.closeBank();
      c.sleep(640);
    }
    if (teleportOut) {
      airCheck();
      earthCheck();
      lawCheck();
    }
  }

  private void eat() {

    int eatLvl = c.getBaseStat(c.getStatId("Hits")) - 20;

    if (c.getCurrentStat(c.getStatId("Hits")) < eatLvl) {

      leaveCombat();
      c.sleep(200);

      c.setStatus("@red@Eating..");

      boolean ate = false;

      for (int id : c.getFoodIds()) {
        if (c.getInventoryItemCount(id) > 0) {
          c.itemCommand(id);
          c.sleep(700);
          ate = true;
        }
      }
      if (!ate) { // only activates if hp goes to -20 again THAT trip, will bank and get new shark
        // usually

        c.setStatus("@red@We've ran out of Food at Hobs! Running Away!.");
        isMining = "none";
        currentOre[0] = 0;
        currentOre[1] = 0;
        c.setStatus("@yel@Banking..");
        hobsToTwenty();

        if (!teleportOut
            || c.getInventoryItemCount(42) < 1
            || c.getInventoryItemCount(33) < 3
            || c.getInventoryItemCount(34) < 1) { // or no earths/airs/laws
          twentyToBank();
        }
        if (teleportOut) {
          c.sleep(100);
          c.castSpellOnSelf(c.getSpellIdFromName("Lumbridge Teleport (1)"));
          c.sleep(800);
          if (c.currentY() < 425) {
            c.castSpellOnSelf(c.getSpellIdFromName("Lumbridge Teleport (2)"));
            c.sleep(800);
          }
          if (c.currentY() < 425) {
            c.castSpellOnSelf(c.getSpellIdFromName("Lumbridge Teleport (3)"));
            c.sleep(1000);
          }
          c.walkTo(120, 644);
          c.atObject(119, 642);
          c.walkTo(217, 447);
          c.sleep(308);
        }
        if (!returnEscape) {
          c.setAutoLogin(
              false); // uncomment and remove bank and banktoHobs to prevent bot going back to mine
          // after being attacked
          c.logout();
          c.sleep(1000);

          if (!c.isLoggedIn()) {
            c.stop();
            c.logout();
          }
        }
        if (returnEscape) {
          bank();
          bankToHobs();
          c.sleep(618);
        }
      }
    }
  }

  private void goToBank() {
    isMining = "none";
    currentOre[0] = 0;
    currentOre[1] = 0;
    c.setStatus("@yel@Banking..");
    hobsToTwenty();
    twentyToBank();
    bank();
    bankToHobs();
    c.sleep(618);
  }

  private void hobsToTwenty() {
    c.setStatus("@gre@Walking to 19 wildy..");
    c.walkTo(221, 262);
    c.walkTo(221, 283);
    c.walkTo(221, 301);
    c.walkTo(221, 314);
    totalTrips = totalTrips + 1;
    c.setStatus("@gre@Done Walking to 19..");
  }

  private void twentyToBank() {
    c.setStatus("@gre@Walking to Bank..");
    eat();
    c.walkTo(221, 321);
    c.walkTo(222, 341);
    c.walkTo(222, 361);
    c.walkTo(222, 381);
    c.walkTo(222, 401);
    c.walkTo(215, 410);
    c.walkTo(215, 420);
    c.walkTo(220, 425);
    c.walkTo(220, 445);
    c.walkTo(217, 448);

    c.setStatus("@gre@Done Walking..");
  }

  private void bankToHobs() {
    c.setStatus("@gre@Walking to Hobs Mine..");
    c.walkTo(218, 447);
    c.walkTo(220, 443);
    c.walkTo(220, 433);
    c.walkTo(220, 422);
    c.walkTo(215, 417);
    c.walkTo(215, 410);
    c.walkTo(215, 401);
    c.walkTo(215, 395);
    eat();
    c.walkTo(222, 388);
    c.walkTo(222, 381);
    c.walkTo(222, 361);
    c.walkTo(222, 341);
    c.walkTo(221, 321);
    c.walkTo(221, 314);
    c.walkTo(221, 301);
    c.walkTo(221, 283);
    c.walkTo(221, 262);

    c.setStatus("@gre@Done Walking..");
  }

  // GUI stuff below (icky)
  private void setupGUI() {
    JLabel header = new JLabel("Hobs Miner - By Kaila");
    JLabel label1 = new JLabel("Start in Edge bank with Armor and Pickaxe");
    JLabel label2 = new JLabel("Sharks in bank REQUIRED");
    JCheckBox teleportCheckbox = new JCheckBox("Teleport if Pkers Attack?", false);
    JLabel label3 = new JLabel("31 Magic, Laws, Airs, and Earths required for Escape Tele");
    JLabel label4 = new JLabel("Unselected, bot WALKS to Edge when Attacked");
    JLabel label5 = new JLabel("Selected, bot walks to 19 wildy and teleports");
    JCheckBox escapeCheckbox = new JCheckBox("Return to Hobs Mine after Escaping?", true);
    JLabel label6 = new JLabel("Unselected, bot will log out after escaping Pkers");
    JLabel label7 = new JLabel("Selected, bot will grab more food and return");
    JLabel label8 = new JLabel("This bot supports the \"autostart\" parameter");
    JLabel label9 = new JLabel("Defaults to Teleport Off, Return On.");
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          teleportOut = teleportCheckbox.isSelected();
          returnEscape = escapeCheckbox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;
        });

    scriptFrame = new JFrame(c.getPlayerName() + " - options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(header);
    scriptFrame.add(label1);
    scriptFrame.add(label2);
    scriptFrame.add(teleportCheckbox);
    scriptFrame.add(label3);
    scriptFrame.add(label4);
    scriptFrame.add(label5);
    scriptFrame.add(escapeCheckbox);
    scriptFrame.add(label6);
    scriptFrame.add(label7);
    scriptFrame.add(label8);
    scriptFrame.add(label9);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {

      String runTime = c.msToString(System.currentTimeMillis() - startTime);
      int coalSuccessPerHr = 0;
      int mithSuccessPerHr = 0;
      int addySuccessPerHr = 0;
      int sapSuccessPerHr = 0;
      int emeSuccessPerHr = 0;
      int rubSuccessPerHr = 0;
      int diaSuccessPerHr = 0;
      int TripSuccessPerHr = 0;
      long timeInSeconds = System.currentTimeMillis() / 1000L;

      try {
        float timeRan = timeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        coalSuccessPerHr = (int) (totalCoal * scale);
        mithSuccessPerHr = (int) (totalMith * scale);
        addySuccessPerHr = (int) (totalAddy * scale);
        sapSuccessPerHr = (int) (totalSap * scale);
        emeSuccessPerHr = (int) (totalEme * scale);
        rubSuccessPerHr = (int) (totalRub * scale);
        diaSuccessPerHr = (int) (totalDia * scale);
        TripSuccessPerHr = (int) (totalTrips * scale);

      } catch (Exception e) {
        // divide by zero
      }
      int x = 6;
      int y = 15;
      c.drawString("@red@Hobs Miner @mag@~ by Kaila", x, y - 3, 0xFFFFFF, 1);
      c.drawString("@whi@____________________", x, y, 0xFFFFFF, 1);
      c.drawString(
          "@whi@Coal Mined: @gre@"
              + totalCoal
              + "@yel@ (@whi@"
              + String.format("%,d", coalSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Coal in Bank: @gre@"
              + coalInBank,
          x,
          y + 14,
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Mith Mined: @gre@"
              + totalMith
              + "@yel@ (@whi@"
              + String.format("%,d", mithSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Mith in Bank: @gre@"
              + mithInBank,
          x,
          y + (14 * 2),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Addy Mined: @gre@"
              + totalAddy
              + "@yel@ (@whi@"
              + String.format("%,d", addySuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Addy in Bank: @gre@"
              + addyInBank,
          x,
          y + (14 * 3),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Sapphires: @gre@"
              + totalSap
              + "@yel@ (@whi@"
              + String.format("%,d", sapSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Emeralds: @gre@"
              + totalEme
              + "@yel@ (@whi@"
              + String.format("%,d", emeSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 4),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Rubys: @gre@"
              + totalRub
              + "@yel@ (@whi@"
              + String.format("%,d", rubSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Diamonds: @gre@"
              + totalDia
              + "@yel@ (@whi@"
              + String.format("%,d", diaSuccessPerHr)
              + "@yel@/@whi@hr@yel@) ",
          x,
          y + (14 * 5),
          0xFFFFFF,
          1);
      c.drawString(
          "@whi@Total Trips: @gre@"
              + totalTrips
              + "@yel@ (@whi@"
              + String.format("%,d", TripSuccessPerHr)
              + "@yel@/@whi@hr@yel@) "
              + "@whi@Runtime: "
              + runTime,
          x,
          y + (14 * 6),
          0xFFFFFF,
          1);
      c.drawString("@whi@____________________", x, y + 3 + (14 * 6), 0xFFFFFF, 1);
    }
  }
}
