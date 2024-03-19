num_replicas=4
count=0
for (( id=0; id<$((num_replicas)); id++ ))
do
	echo "Starting layer 1 replica $id"
  ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer $((id)) /Users/roy/Github-Repos/BFT-SMART/src/main/java/bftsmart/demo/counter/layerConfigs/layer1.properties
	count=$((count+1))
	echo $id


done
count=0
max_replicas=$((num_replicas + count))
for (( id=$((count)); id<$((max_replicas)); id++ ))
do
	echo "Starting layer 2 replica $id"
  	ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer $((id)) /Users/roy/Github-Repos/BFT-SMART/src/main/java/bftsmart/demo/counter/layerConfigs/layer2.properties
	echo $id

done

count=0
max_replicas=$((num_replicas + count))
for (( id=$((count)); id<$((max_replicas)); id++ ))
do
	echo "Starting layer 3 replica $id"
  	ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer $((id)) /Users/roy/Github-Repos/BFT-SMART/src/main/java/bftsmart/demo/counter/layerConfigs/layer3.properties
	echo $id

done

#ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 0
#ttab -t 'R1' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 1
#ttab -t 'R2' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 2
#ttab -t 'R3' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 3




#ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 0 /Users/roy/Github-Repos/BFT-SMART/src/main/java/bftsmart/demo/counter/layerConfigs/layer1.properties
#ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 2 /Users/roy/Github-Repos/BFT-SMART/src/main/java/bftsmart/demo/counter/layerConfigs/layer1.properties
#ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 3 /Users/roy/Github-Repos/BFT-SMART/src/main/java/bftsmart/demo/counter/layerConfigs/layer1.properties
#
#ttab -t 'R0' runscripts/smartrun.sh bftsmart.demo.counter.CounterServer 1 /Users/roy/Github-Repos/BFT-SMART/src/main/java/bftsmart/demo/counter/layerConfigs/layer1.properties