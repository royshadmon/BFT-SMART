num_replicas=3
count=0
for (( id=0; id<$((num_replicas)); id++ ))
do
	echo "Starting layer 1 replica $id"
  	ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer $((id)) /Users/royshadmon/BFT-SMART/config/hosts.config
	count=$((count+1))
	echo $id

done
#count=0
#max_replicas=$((num_replicas + count))
#for (( id=$((count)); id<$((max_replicas)); id++ ))
#do
#	echo "Starting layer 1 replica $id"
#  	ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer $((id)) /Users/royshadmon/BFT-SMART/config/hosts-l2.config
#	echo $id
#
#done

#ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 0
#ttab -t 'R1' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 1
#ttab -t 'R2' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 2
#ttab -t 'R3' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 3
