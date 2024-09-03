class TestThread3a extends Thread{
	
	//Run the computation
	public void run(){
		 computation(5);
		 SysLib.cout("Compute Thread Done!\n");
		 SysLib.exit();
	}
	
	//computation
	public void computation(int n){
		if (n <= 0){
			return;
		}
		for(int i = 0; i < n; i++){
			computation(n - 1);
		}
	}
}
