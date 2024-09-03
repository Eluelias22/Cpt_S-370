
class TestThread3b extends Thread{
	
	//A block of data to write to disk
	byte[] buffer = new byte[512];
	
	//write, read and exit
	public void run(){
		for(int i = 0; i < 1000; i++){
			SysLib.rawwrite(i,buffer);
			SysLib.rawread(i,buffer);
		}
		SysLib.cout("Disk Thread Done!\n");
		SysLib.exit();	
	}
}
