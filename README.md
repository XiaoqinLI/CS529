# CS529
BlueTooth_Network_Project

## Xiaoqin's to do list (before Mid-Term):
GitHub (Done)

Development Environment (Eclipse Done)

BlueTooth (android) (Done) 

Service (android) (Done)

figure out how to multi connect (Done with bug)

delete popup and confirmation function when connecting. (Done)


##********Do do and Bug List*********************
implememnt discover list and connection list (low priority for now, just modify stuff in DeviceListActivity):

To get all paired devices, what we do:
if (pairedDevices.size() > 0) {
	findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
	for (BluetoothDevice device : pairedDevices) {
		pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	}
} else {
		String noDevices = getResources().getText(R.string.none_paired).toString();
		pairedDevicesArrayAdapter.add(noDevices);
}

To get all discoverable device, what we do:




Why mChatService is gone when exiting app??



