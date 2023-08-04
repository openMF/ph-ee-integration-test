#!/bin/bash

# Replace "YOUR_NGROK_AUTH_TOKEN" with your actual ngrok.com API key
# Replace 3000 with your desired local port

# Start ngrok and capture the output in a variable
ngrok_output=$(ngrok http 53013)

# Extract the public URL from the ngrok output
public_url=$(echo "$ngrok_output" | grep -Eo 'https://[^ ]+' | tail -n 1)

# Set the public URL as an environment variable
export NGROK_PUBLIC_URL="$public_url"

# Display the URL (optional)
echo "Tunnel started! Public URL: $NGROK_PUBLIC_URL"

# Wait for 5 minutes (300 seconds)
sleep 600

# Stop the ngrok process by sending a SIGINT signal (Ctrl+C)
killall ngrok
