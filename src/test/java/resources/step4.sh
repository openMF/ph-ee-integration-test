
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
      exec kubectl exec -i -t -n default ph-ee-connector-ams-mifos-84c7796c45-vzf7s -c ph-ee-connector-ams-mifos --as=system:serviceaccount:payment-hub:ph-ee-operator-c-role -- sh -c "clear; (bash || ash || sh)"
      if [ $? -eq 0 ]; then
          echo "done step 4"
      else
          echo "failed step 4"
      fi
  fi