import React ,{useState} from 'react';
import { SafeAreaView, View, Text, NativeModules, Platform, TouchableOpacity ,StyleSheet } from 'react-native';


export const App = () => {
  const [ispath ,setIsPath] = useState('');


  const onPay = () => {
    let pyData = {
      checkoutId:  "7AA1F307B9E9773928426933AF17D198.uat01-vm-tx03"
    }

    if (Platform.OS === 'android') {
      console.log("payment details", NativeModules)
      NativeModules.OppwaNativeMethodModule.openHyperPay(pyData, (res) => {
        console.log("response android native", res)
      }, (err) => {
        console.log("error android native", err)
      })
    } else {
      NativeModules.OppwaNativeModule.openHyperPay(pyData, (res) => {
        console.log("response ios native", res)
        setIsPath(res.resourcePath)
      }, (err) => {
        console.log("error ios native", err)
      })

    }

  }
  return (
    <SafeAreaView style={{ flex: 1 }}>
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <View style={{borderWidth : 1,padding : 15,}}>
            <Text>{ "Resource PAth :"+ ispath}</Text>
        </View>
        <View style={{height : 90}} />
        <TouchableOpacity onPress={onPay} style={styles.btnCont}>
          <Text style={styles.paynow} >{"Pay Now"}</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  )
}

const styles = StyleSheet.create({
  btnCont : {
    height: 55, width: '75%', borderWidth: 1, borderRadius: 12, justifyContent: 'center', alignItems: 'center'
  },
  paynow:{
    fontSize : 16 ,fontWeight : '700',
  }
})