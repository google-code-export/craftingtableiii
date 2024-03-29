package lukeperkin.craftingtableii;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_CraftingTableIII;

public class TileEntityCraftingTableII extends TileEntity implements IInventory {
	
	public double playerDistance;
	public float doorAngle;
	public static final float openspeed = 0.2F; 
	private int tablestate;
	public ItemStack[] theInventory = new ItemStack[19];
	
	public TileEntityCraftingTableII() {
		playerDistance = 7F;
		doorAngle = 0F;
		tablestate = 0;
	}
	
	public int getFacing()
	{
		return getBlockMetadata();
	}
	
	public void updateEntity()
	{
		super.updateEntity();
		if (mod_CraftingTableIII.EnableDoor) {
			EntityPlayer entityplayer = worldObj.getClosestPlayer((float)xCoord + 0.5F, (float)yCoord + 0.5F, (float)zCoord + 0.5F, 10D);
			if(entityplayer != null){
				playerDistance = entityplayer.getDistanceSq((double)xCoord, (double)yCoord, (double)zCoord);
				if(playerDistance < 7F){
					doorAngle += openspeed;
					
					if(tablestate != 1) {
						tablestate = 1;
						if (mod_CraftingTableIII.EnableSound)
							worldObj.playSoundEffect((double)xCoord, (double)yCoord + 0.5D, (double)zCoord, "random.chestopen", 0.2F, worldObj.rand.nextFloat() * 0.1F + 0.2F);
					}
					
					if(doorAngle > 1.8F){
						doorAngle = 1.8F;
					}
				} else if(playerDistance > 7F) {
					doorAngle -= openspeed;
					
					if(tablestate != 0) {
						tablestate = 0;
						if (mod_CraftingTableIII.EnableSound)
							worldObj.playSoundEffect((double)xCoord, (double)yCoord + 0.5D, (double)zCoord, "random.chestclosed", 0.2F, worldObj.rand.nextFloat() * 0.1F + 0.2F);
					}
					
					if(doorAngle < 0F){
						doorAngle = 0F;
					}
				}
			}
		}
	}
	 public void readFromNBT(NBTTagCompound nbttagcompound) {
	     super.readFromNBT(nbttagcompound);
	     
	     NBTTagList nbttaglist = nbttagcompound.getTagList("stackList");
	     
	     theInventory = new ItemStack [nbttaglist.tagCount()];
	     
	     for (int i = 0; i < theInventory.length; ++i) {  
	    	 NBTTagCompound nbttagcompound2 = (NBTTagCompound) nbttaglist
				.tagAt(i);	
	    	 
	    	 if (!nbttagcompound2.getBoolean("isNull")) {
	    		 theInventory [i] = ItemStack.loadItemStackFromNBT(nbttagcompound2);
	    	 }
	     }
	 }

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		
    	NBTTagList nbttaglist = new NBTTagList();
    	
    	for (int i = 0; i < theInventory.length; ++i) {    		
    		NBTTagCompound nbttagcompound2 = new NBTTagCompound ();
    		nbttaglist.appendTag(nbttagcompound2);
    		if (theInventory[i] == null) {
    			nbttagcompound2.setBoolean("isNull", true);
    		} else {
    			nbttagcompound2.setBoolean("isNull", false);
    			theInventory[i].writeToNBT(nbttagcompound2);
    		}
    		
    	}
    	
    	nbttagcompound.setTag("stackList", nbttaglist);
	}
	
	
	@Override
	public int getSizeInventory() {

		return theInventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return theInventory [i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {

		ItemStack newStack = theInventory[i].copy();
		newStack.stackSize = j;
		
		theInventory [i].stackSize -= j;
		
		if (theInventory[i].stackSize == 0) {
			theInventory[i] = null;
		}
		
		return newStack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		theInventory [i] = itemstack;
	}

	@Override
	public String getInvName() {
	
		return "";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int var1){
		if (this.theInventory[var1] == null) return null;
		ItemStack stack = this.theInventory[var1];
		this.theInventory[var1] = null;
		return stack;
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}
	public int getFreeSlot()
	{
		for (int i=0; i < this.getSizeInventory()-1; i++)
		{
			if (getStackInSlot(i) == null)
				return i;
		}
		return -1;
	}
	public int FindStack(ItemStack aStack)
	{
		for (int i=0; i < this.getSizeInventory()-1; i++)
		{
			if (getStackInSlot(i) != null)
				if (getStackInSlot(i).itemID == aStack.itemID)
					if (getStackInSlot(i).getHasSubtypes())
					{
						if (getStackInSlot(i).getItemDamage() == aStack.getItemDamage())
							if (getStackInSlot(i).getMaxStackSize() > getStackInSlot(i).stackSize)
								return i;
					} else {
						if (getStackInSlot(i).getMaxStackSize() > getStackInSlot(i).stackSize)
							return i;
					}
		}
		return -1;
	}
	public boolean addItemStackToInventory(ItemStack aStack)
    {
		int Index = FindStack(aStack);
		while (Index > -1)
		{
			ItemStack tarStack = getStackInSlot(Index);
			if (tarStack.isStackable())
				if (tarStack.getMaxStackSize() - tarStack.stackSize >= aStack.stackSize)
				{
					tarStack.stackSize += aStack.stackSize;
					aStack.stackSize = 0;
					setInventorySlotContents(Index, tarStack);
				} else {
					aStack.stackSize -= (tarStack.getMaxStackSize() - tarStack.stackSize);
					tarStack.stackSize = tarStack.getMaxStackSize();
					setInventorySlotContents(Index, tarStack);
				}
			if (aStack.stackSize <= 0)
				return true;
			Index = FindStack(aStack);
		}
		if (aStack.stackSize > 0) {
			Index = getFreeSlot();
			if (Index > -1)
			{
				setInventorySlotContents(Index, aStack);
			} else {
				return false;
			}
		}
		return true;
    }
	public TileEntityCraftingTableII getCopy()
	{
		TileEntityCraftingTableII Clone = new TileEntityCraftingTableII();
		for (int i=0; i < this.getSizeInventory(); i++)
		{
			if (getStackInSlot(i) != null)
				Clone.setInventorySlotContents(i, getStackInSlot(i).copy());
			else
				Clone.setInventorySlotContents(i, null);
		}
		return Clone;
	}
}
