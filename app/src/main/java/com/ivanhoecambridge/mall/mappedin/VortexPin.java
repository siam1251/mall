package com.ivanhoecambridge.mall.mappedin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.ivanhoecambridge.mall.R;
import com.mappedin.sdk.Coordinate;
import com.mappedin.sdk.Instruction;

/**
 * Created by Kay on 2016-12-13.
 */

public class VortexPin {

    protected static final String TAG = "VortexPin";
    Context context;
    Instruction.TakeVortex takeVortex;
    VortexDirection vortexDirection;
    VortexType vortexType;
    Drawable vortexDrawable;
    Coordinate vortexCoordinate;
    Pin vortexPin;


    static enum VortexDirection {
        DOWN,
        UP,
        UNKNOWN;

        private VortexDirection() {
        }
    }

    static enum VortexType {
        TENANT,
        ELEVATOR,
        ESCALATOR,
        ESCALATOR_STAIRS,
        STAIRS,
        UNKNOWN;

        private VortexType() {
        }
    }

    public VortexPin(Context context, Instruction instruction){
        this.context = context;
        takeVortex = (Instruction.TakeVortex) instruction.action;
        if(takeVortex.fromMap.getElevation() > takeVortex.toMap.getElevation()) {
            vortexDirection = VortexDirection.DOWN;
        } else {
            vortexDirection = VortexDirection.UP;
        }
        vortexType = getVortexType(instruction.atLocation.getType());
        vortexDrawable = getVortexDrawable(instruction.atLocation.getType());
        vortexCoordinate = instruction.coordinate;
    }

    public static boolean isVortex(Instruction instruction){
        return instruction.action instanceof Instruction.TakeVortex;
    }

    public VortexType getVortexType(String vortexTypeString){
        if(vortexTypeString.equalsIgnoreCase("tenant")) return VortexType.TENANT;
        else if(vortexTypeString.equalsIgnoreCase("elevator")) return VortexType.ELEVATOR;
        else if(vortexTypeString.equalsIgnoreCase("escalator")) return VortexType.ESCALATOR;
        else if(vortexTypeString.equalsIgnoreCase("stairs")) return VortexType.STAIRS;
        else if(vortexTypeString.equalsIgnoreCase("escalator/stairs")) return VortexType.ESCALATOR_STAIRS;
        else return VortexType.UNKNOWN;
    }

    public Drawable getVortexDrawable(String vortexTypeString){
        Resources resources = context.getResources();
        if(vortexType.equals(VortexType.ELEVATOR)) {
            if(vortexDirection.equals(VortexDirection.UP)) return resources.getDrawable(R.drawable.icn_elevator_up);
            else return resources.getDrawable(R.drawable.icn_elevator_down);
        } else if (vortexType.equals(VortexType.ESCALATOR) || vortexType.equals(VortexType.ESCALATOR_STAIRS)){
            if(vortexDirection.equals(VortexDirection.UP)) return resources.getDrawable(R.drawable.icn_escalator_up);
            else return resources.getDrawable(R.drawable.icn_escalator_down);
        } else if (vortexType.equals(VortexType.STAIRS)){
            if(vortexDirection.equals(VortexDirection.UP)) return resources.getDrawable(R.drawable.icn_stair_up);
            else return resources.getDrawable(R.drawable.icn_stair_down);
        } else {
            Log.e(TAG, "error getting pinDrawable");
            return resources.getDrawable(R.drawable.icn_elevator_up);
        }
    }

    public Coordinate getVortexCoordinate(){
        return vortexCoordinate;
    }

    public Drawable getVortexDrawable(){
        return vortexDrawable;
    }

    public void setVortexPin(Pin pin){
        vortexPin = pin;
    }

    public Pin getVortexPin(){
        return vortexPin;
    }
}
