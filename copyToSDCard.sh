[ -z "$1" ] && { echo "Missing Argument: SD Card Mount Directory"; exit; }

[ -d $1 ] && echo "Found directory $1..." || { echo "ERROR: Directory $1 does not exist."; exit; }

[ ! -d $1/nirootfs/home/lvuser ] && { echo "ERROR: could not find $1/nirootfs/home/lvuser. Is this the correct image?"; exit; }

if [[ $2 == "-s" ]]
then
    echo "Connecting to 10.8.62.2..."
    sudo scp lvuser@10.8.62.2:void.jar $1/nirootfs/home/lvuser
    sudo scp -r lvuser@10.8.62.2:deploy/paths $1/nirootfs/home/lvuser
else
    echo "Copying to $1..."  
    sudo cp ./build/libs/void.jar $1/nirootfs/home/lvuser
    sudo cp -R ./PathWeaver/Paths $1/nirootfs/home/lvuser
fi
