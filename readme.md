# [Platinum Eyes](https://github.com/mew6210/Platinum-Eyes) Performance Profiler

## Small app for testing PE performance, possibly I will add it as GitHub Actions for PE directly.

# How to make it work:

### Inputting a collection of screenshots to analyze
 - Add folder "collections" to where jar is executed
 - Then add folder "1" to the folder collections and dump all the cropped screenshots u want to analyze into it
 - Additionally, add file "collection.txt" to "1" that describes what is in the pictures like so:
   <br>
   <br>
   collections/1/collection.txt: 
   ```
   file: screenshot1 
   item1: Forma Blueprint 
   item2: Acceltra Prime Blueprint 
   item3: Forma Blueprint 
   item4: Forma Blueprint 
    
   file: screenshot2 
   item1: Magnus Prime Blueprint 
   item2: Nautilus Prime Carapace 
    
   file: screenshot3 
   item1: Ninkondi Prime Chain 
   item2: Fulmin Prime Blueprint 
   item3: Nautilus Prime Carapace
   ```
 - Where screenshot10 is a name for a file "screenshot10.bmp"
 - Each file needs to be .bmp, but their name doesn't matter

### Providing Platinum Eyes binary
 - **Also, to make it work you need an actual PE binary downloaded and pasted into where jar is executed, entire folder Platinum-Eyes/**
 - Ideally, PE should not be configured in any way. A fresh installation is perfect, but just deleting tool_config.txt from PE directory also works (it will be regenerated).

### After that
 - Your directory should look like that:
```
   
   PEProfiler.jar          //The executable of this project
   collections/            //Your input of cropped screenshots
   -- 1/
   ---- collections.txt
   ---- screenshot1.bmp    //Example names for screenshots
   ---- screenshot2.bmp
   Platinum-Eyes/          //Fresh install of Platinum Eyes
   -- Platinum_Eyes.exe
   -- ..other PE files..
```
 - When all that is done, PEProfiler should be ready to execute. When it runs, u should not press anything as it is pressing alt + x to invoke screenshot reading. You should wait until you know its done or about 200ms per picture u have inputted.
 - You should have as a result report.md or other form of report in the jar directory, providing u with insights about the screenshot session that was just executed.

## Upcoming features:
 - Automatic screenshot taking -> This app currently relies on user providing cropped screenshots, which is very uncomfortable. In the future this app will be able to make Platinum-Eyes also take pictures, not only analyze them
 - More report types -> I plan on making more ways of generating a report, not just Markdown
 - Platinum Eyes GitHub Actions integration -> There is a possibility of hooking this app up to GitHub Actions and have it run as performance checker
 - Customization -> Make the app more responsive to user input through command line arguments, for example providing path to PE would save up either disk space (around 30MB) or user's patience of moving their PE to other directories
 - Multiple runs -> Make the app able to run the same collection reading multiple times, to take the average of those performances, as reading with ocr is often hit or miss
 - and other like better PE integration of listening to entire logs or analyzing how many items were correctly fuzzy searched
