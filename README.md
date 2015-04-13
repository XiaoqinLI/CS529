# CS529
BlueTooth_Network_Project

## Xiaoqin's to do list (before Mid-Term):
GitHub (Done)

Development Environment (Eclipse Done)

BlueTooth (android) (Done) 

Service (android) (Done)

figure out how to multi connect (Done with bug)

delete popup and confirmation function when connecting. (Done)

update discover list and real pair list (Done)

##********To do and Bug List*********************

Why mChatService is gone when exiting app?? how to not exit.
	what I knew: it has nothing to do with DeviceListActivity.
	when exiting app, it calls oncreat, onStart, onResume again...
	mCharservice somehow got vanished, it is gone, so in onStart, setupChat() is recalled when we initialize a new empty mCharservice...
	Extend service ? IBinder?


Multi connect Maintaining

add connected list.



