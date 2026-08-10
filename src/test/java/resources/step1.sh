  kubectl version
  if [ $? -eq 0 ]; then
      echo "kubectl version is installed"
  else
      echo "kubectl version is not installed"
      curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.22.17/2023-05-11/bin/darwin/amd64/kubectl
      curl -O https://s3.us-west-2.amazonaws.com/amazon-eks/1.22.17/2023-05-11/bin/darwin/amd64/kubectl.sha256
      openssl sha1 -sha256 kubectl
      chmod +x ./kubectl
      $HOME = /usr/local/
      mkdir -p $HOME/bin && cp ./kubectl $HOME/bin/kubectl && export PATH=$HOME/bin:$PATH
      kubectl version
      kubectl config use-context arn:aws:eks:us-east-2:419830066942:cluster/sit
      if [ $? -eq 0 ]; then
          echo "kubectl version is installed"
      else
          echo "kubectl version is not installed"
      fi
  fi





