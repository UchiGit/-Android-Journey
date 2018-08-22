package com.example.g015c1140.journey

class Setting {

    val IP_ADDRESS = "IP ADDRESS"
    val USER_ID = ""

    val SPOT_POST_URL = "http://$IP_ADDRESS:3000/api/v1/spot/register/"
    val SPOT_GET_URL = "http://$IP_ADDRESS:3000/api/v1/spot/find?user_id=$USER_ID"
    val PLAN_POST_URL = "http://$IP_ADDRESS:3000/api/v1/plan/register/"

}