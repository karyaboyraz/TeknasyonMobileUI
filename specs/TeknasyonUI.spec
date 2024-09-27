TeknasyonUI
=====================
Created by karyaboyraz on 27.09.2024

This is an executable specification file which follows markdown syntax.
Every heading in this file denotes a scenario. Every bulleted point denotes a step.

Scenario: 1
----------------
*Click button "App"
*Click button "Activity"
*Click button "Custom Title"
*Write text "Left isnt always cool" to this element "left_text_edit"
*Click button "Change Left"
*Get Text Value from "left_text" and compare with "Left isnt always cool"
*Write text "Right better than left" to this element "right_text_edit"
*Click button "Change Right"
*Get Text Value from "right_text" and compare with "Right better than left"


Scenario: 2
----------------
*Click button "App"
*Click button "Alert Dialogs"
*Click button "List dialog"
*Click button "Command one"
*Check element existence "You selected: 0 , Command one" must be "visible"


Scenario: 3
----------------
*Click button "App"
*Click button "Fragment"
*Click button "Context Menu"
*Long press on element "Long press me"
*Check element existence "Menu A" must be "visible"
*Check element existence "Menu B" must be "visible"

Scenario: 4
----------------
*Click button "App"
*Click button "Fragment"
*Click button "Hide and Show"
*Check element existence "The fragment saves and restores this text." must be "visible"
*Get Text Value from "frag1hide" and compare with "Hide"
*Click button "frag1hide"
*Check element existence "The fragment saves and restores this text." must be "hidden"
*Get Text Value from "frag1hide" and compare with "Show"
*Click button "frag1hide"
*Check element existence "The fragment saves and restores this text." must be "visible"

Scenario: 5
----------------
*Click button "App"
*Click button "Notification"
*Click button "IncomingMessage"
*Click button "Show App Notification"
*Open notifications bar by swiping down
*Get text and title value from Notifications and store it
*Click button "status_bar_latest_event_content"
*Check Notifications title and text values

Scenario: 6
----------------
Tags: reinstall

*Click button "Views"
*Search Element "Tabs" with Swipe "15" times
*Click button "Tabs"
*Click button "Scrollable"
*Search Element "TAB 30" with Horizontal Swipe "15" times
