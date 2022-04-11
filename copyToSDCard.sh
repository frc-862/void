[ -z "$1" ] && { echo "Missing Argument: SD Card Directory"; exit; }

[ -d $1 ] && echo "Found directory $1..."\
|| { echo "ERROR: Directory $1 does not exist."; exit; }

[ ! -d $1/lvuser ] &&\
{ echo "ERROR: could not find $1/lvuser. Is this the correct image?"; exit; }

if [[ $2 == "-s" ]]
then
    echo "Connecting to 10.8.62.2..."
    scp lvuser@10.8.62.2:void.jar $1/lvuser
    scp -r lvuser@10.8.62.2:deploy/paths $1/lvuser
else
    echo "Copying to $1..."  
    cp ./build/libs/void.jar $1/lvuser
    cp -R ./PathWeaver/Paths $1/lvuser
fi
